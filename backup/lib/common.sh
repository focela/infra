#!/usr/bin/env bash
# Shared helpers for per-stack backup scripts.
#
# Usage from backup/<stack>-backup.sh:
#   STACK_NAME="proxy"
#   STACK_DATA_VAR="PROXY_DATA_DIR"     # data-dir env var in stacks/<stack>/.env
#   ARCHIVE_PREFIX="proxy-backup"       # archive filename prefix
#   PRUNE_PATTERN='^(proxy-backup|npm-backup)-'  # retention filename regex
#   source "${SCRIPT_DIR}/lib/common.sh"
#   backup_init "$@"
#   ... optional pre-archive hook ...
#   backup_create_archive
#   ... optional post-archive hook ...
#   backup_upload
#   backup_prune
#
# Globals set by backup_init:
#   DRY_RUN              true|false
#   BACKUP_S3_BUCKET     required, from backup/.env
#   BACKUP_RETENTION     default 14, from backup/.env
#   STACK_DIR            stacks/<stack>/, override: <STACK>_STACK_DIR
#   STACK_ENV_FILE       stacks/<stack>/.env
#   COMPOSE_FILE         stacks/<stack>/compose.yaml
#   DATA_DIR             absolute path, override: <STACK>_BACKUP_DATA_DIR
#   TMP_DIR              private working directory; caller removes it
#   ARCHIVE_NAME         <ARCHIVE_PREFIX>-<UTC timestamp>.tar.gz
#   TMP_ARCHIVE          ${TMP_DIR}/${ARCHIVE_NAME}

set -euo pipefail

backup_log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*"; }
backup_run() { if [[ "${DRY_RUN}" == "true" ]]; then backup_log "[dry-run] $*"; else "$@"; fi; }

backup_require_commands() {
  for cmd in "$@"; do
    if ! command -v "${cmd}" &>/dev/null; then
      echo "error: '${cmd}' not found in PATH" >&2
      exit 1
    fi
  done
}

backup_compose() {
  local env_args=()
  [[ -f "${STACK_ENV_FILE}" ]] && env_args=(--env-file "${STACK_ENV_FILE}")
  docker compose "${env_args[@]}" -f "${COMPOSE_FILE}" "$@"
}

backup_init() {
  : "${STACK_NAME:?STACK_NAME is required}"
  : "${ARCHIVE_PREFIX:?ARCHIVE_PREFIX is required}"

  # Load backup/.env from the backup directory; export values for AWS CLI.
  local script_dir
  script_dir="$(cd "$(dirname "${BASH_SOURCE[1]}")" && pwd)"
  REPO_ROOT="$(cd "${script_dir}/.." && pwd)"
  if [[ -f "${script_dir}/.env" ]]; then
    set -a
    # shellcheck source=/dev/null
    source "${script_dir}/.env"
    set +a
  fi

  DRY_RUN=false
  [[ "${1:-}" == "--dry-run" ]] && DRY_RUN=true

  BACKUP_S3_BUCKET="${BACKUP_S3_BUCKET:?BACKUP_S3_BUCKET is required (e.g. s3://my-bucket/infra-backups)}"
  BACKUP_RETENTION="${BACKUP_RETENTION:-14}"

  # Stack paths: <STACK>_STACK_DIR overrides BACKUP_STACK_DIR overrides default.
  # Use tr for uppercase: ${var^^} is Bash 4+ and breaks on macOS Bash 3.2.
  local upper
  upper="$(echo "${STACK_NAME}" | tr '[:lower:]' '[:upper:]')"
  local stack_override="${upper}_STACK_DIR"
  STACK_DIR="${!stack_override:-${BACKUP_STACK_DIR:-${REPO_ROOT}/stacks/${STACK_NAME}}}"
  STACK_ENV_FILE="${STACK_DIR}/.env"
  COMPOSE_FILE="${STACK_DIR}/compose.yaml"

  # Load stack .env before resolving the data directory.
  if [[ -f "${STACK_ENV_FILE}" ]]; then
    # shellcheck source=/dev/null
    source "${STACK_ENV_FILE}"
  fi

  # Resolution order: <STACK>_BACKUP_DATA_DIR > BACKUP_DATA_DIR > <STACK_DATA_VAR> > ./data
  local data_override="${upper}_BACKUP_DATA_DIR"
  local stack_default=""
  [[ -n "${STACK_DATA_VAR:-}" ]] && stack_default="${!STACK_DATA_VAR:-}"
  local resolved="${!data_override:-${BACKUP_DATA_DIR:-${stack_default:-./data}}}"
  if [[ "${resolved}" == /* ]]; then
    DATA_DIR="${resolved}"
  else
    DATA_DIR="${STACK_DIR}/${resolved}"
  fi

  TIMESTAMP="$(date -u +%Y%m%d-%H%M%SZ)"
  ARCHIVE_NAME="${ARCHIVE_PREFIX}-${TIMESTAMP}.tar.gz"

  backup_require_commands aws tar
  [[ -d "${DATA_DIR}" ]] || { echo "error: data directory not found: ${DATA_DIR}" >&2; exit 1; }

  # Keep the archive private while it is staged under /tmp.
  TMP_DIR="$(mktemp -d)"
  TMP_ARCHIVE="${TMP_DIR}/${ARCHIVE_NAME}"
}

backup_create_archive() {
  backup_log "Creating archive: ${TMP_ARCHIVE}"
  local parent base
  parent="$(cd "$(dirname "${DATA_DIR}")" && pwd)"
  base="$(basename "${DATA_DIR}")"
  backup_run tar -czf "${TMP_ARCHIVE}" -C "${parent}" "${base}/"
}

backup_upload() {
  backup_log "Uploading ${ARCHIVE_NAME} to ${BACKUP_S3_BUCKET}"
  backup_run aws s3 cp "${TMP_ARCHIVE}" "${BACKUP_S3_BUCKET}/${ARCHIVE_NAME}"
}

backup_prune() {
  local pattern="${1:?prune pattern required}"
  backup_log "Pruning backups older than ${BACKUP_RETENTION} days"
  # GNU date and BSD date take different flags; try GNU first, fall back to BSD.
  local cutoff
  cutoff="$(date -u -d "-${BACKUP_RETENTION} days" +%Y-%m-%dT%H:%M:%SZ 2>/dev/null \
    || date -u -v "-${BACKUP_RETENTION}d" +%Y-%m-%dT%H:%M:%SZ)"

  if [[ "${DRY_RUN}" == "true" ]]; then
    backup_log "[dry-run] would prune objects before ${cutoff}"
    return
  fi

  local cutoff_ts
  cutoff_ts="$(date -u -d "${cutoff}" +%s 2>/dev/null \
    || date -u -j -f "%Y-%m-%dT%H:%M:%SZ" "${cutoff}" +%s)"

  while read -r obj; do
    # Extract timestamp from filename: <prefix>-YYYYMMDD-HHMMSSZ.tar.gz
    [[ "${obj}" =~ ([0-9]{8}-[0-9]{6}Z) ]] || continue
    local obj_ts="${BASH_REMATCH[1]}"
    local obj_dt
    obj_dt="$(date -u -d "${obj_ts:0:8} ${obj_ts:9:2}:${obj_ts:11:2}:${obj_ts:13:2}" +%s 2>/dev/null \
      || date -u -j -f "%Y%m%d%H%M%S" "${obj_ts:0:8}${obj_ts:9:6}" +%s)"
    if [[ "${obj_dt}" -lt "${cutoff_ts}" ]]; then
      backup_log "Deleting old backup: ${obj}"
      aws s3 rm "${BACKUP_S3_BUCKET}/${obj}"
    fi
  done < <(aws s3 ls "${BACKUP_S3_BUCKET}/" | awk '{print $4}' | grep -E "${pattern}" || true)
}
