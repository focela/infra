#!/usr/bin/env bash
# npm-backup.sh — offline backup for the NPM stack (SQLite + certs + nginx config).
#
# Strategy: stop container → tar ./data → upload to S3 → restart → prune old backups.
# Downtime is ~5-10 s. For zero-downtime, use the SQLite .backup API instead
# if zero-downtime is required and sqlite3 is available on the host.
#
# Usage:
#   ./backup/npm-backup.sh [--dry-run]
#
# Environment (set in .env or export before running):
#   BACKUP_S3_BUCKET   required  s3://your-bucket/path
#   BACKUP_RETENTION   optional  number of days to keep backups (default: 14)
#   BACKUP_STACK_DIR   optional  path to stacks/proxy (default: auto-detected)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

if [[ -f "${SCRIPT_DIR}/.env" ]]; then
  # shellcheck source=/dev/null
  source "${SCRIPT_DIR}/.env"
fi

DRY_RUN=false
if [[ "${1:-}" == "--dry-run" ]]; then
  DRY_RUN=true
fi

BACKUP_S3_BUCKET="${BACKUP_S3_BUCKET:?BACKUP_S3_BUCKET is required (e.g. s3://my-bucket/npm-backups)}"
BACKUP_RETENTION="${BACKUP_RETENTION:-14}"
BACKUP_STACK_DIR="${BACKUP_STACK_DIR:-${REPO_ROOT}/stacks/proxy}"
COMPOSE_FILE="${BACKUP_STACK_DIR}/compose.yaml"
DATA_DIR="${BACKUP_STACK_DIR}/data"
TIMESTAMP="$(date -u +%Y%m%d-%H%M%SZ)"
ARCHIVE_NAME="npm-backup-${TIMESTAMP}.tar.gz"
TMP_ARCHIVE="/tmp/${ARCHIVE_NAME}"

log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*"; }
run()  { if $DRY_RUN; then log "[dry-run] $*"; else "$@"; fi; }

for cmd in docker aws tar; do
  if ! command -v "$cmd" &>/dev/null; then
    echo "error: '$cmd' not found in PATH" >&2
    exit 1
  fi
done

if [[ ! -d "${DATA_DIR}" ]]; then
  echo "error: data directory not found: ${DATA_DIR}" >&2
  exit 1
fi

log "Starting NPM backup → ${BACKUP_S3_BUCKET}/${ARCHIVE_NAME}"

# Stop the stack so SQLite is in a clean state (no in-flight writes).
log "Stopping npm stack"
run docker compose -f "${COMPOSE_FILE}" down

# Archive the entire data directory (SQLite DB + letsencrypt certs + nginx configs).
log "Creating archive: ${TMP_ARCHIVE}"
run tar -czf "${TMP_ARCHIVE}" -C "${BACKUP_STACK_DIR}" data/

# Restart before the upload so downtime is minimal.
log "Restarting npm stack"
run docker compose -f "${COMPOSE_FILE}" up -d

log "Uploading ${ARCHIVE_NAME} to ${BACKUP_S3_BUCKET}"
run aws s3 cp "${TMP_ARCHIVE}" "${BACKUP_S3_BUCKET}/${ARCHIVE_NAME}"

run rm -f "${TMP_ARCHIVE}"

log "Pruning backups older than ${BACKUP_RETENTION} days"
CUTOFF="$(date -u -d "-${BACKUP_RETENTION} days" +%Y-%m-%dT%H:%M:%SZ 2>/dev/null \
  || date -u -v "-${BACKUP_RETENTION}d" +%Y-%m-%dT%H:%M:%SZ)"  # GNU/BSD compat

if $DRY_RUN; then
  log "[dry-run] would prune objects before ${CUTOFF}"
else
  aws s3 ls "${BACKUP_S3_BUCKET}/" \
    | awk '{print $4}' \
    | grep '^npm-backup-' \
    | while read -r obj; do
        # Extract timestamp from filename: npm-backup-YYYYMMDD-HHMMSSz.tar.gz
        obj_ts="$(echo "${obj}" | grep -oP '\d{8}-\d{6}Z')" || continue
        obj_dt="$(date -u -d "${obj_ts:0:8} ${obj_ts:9:2}:${obj_ts:11:2}:${obj_ts:13:2}" +%s 2>/dev/null \
          || date -u -j -f "%Y%m%d%H%M%S" "${obj_ts:0:8}${obj_ts:9:6}" +%s)"
        cutoff_ts="$(date -u -d "${CUTOFF}" +%s 2>/dev/null || date -u -j -f "%Y-%m-%dT%H:%M:%SZ" "${CUTOFF}" +%s)"
        if [[ "${obj_dt}" -lt "${cutoff_ts}" ]]; then
          log "Deleting old backup: ${obj}"
          aws s3 rm "${BACKUP_S3_BUCKET}/${obj}"
        fi
      done
fi

log "Backup complete: ${ARCHIVE_NAME}"
