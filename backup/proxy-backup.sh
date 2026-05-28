#!/usr/bin/env bash
# Backup for proxy SQLite, certificates, and nginx config.
#
# Stop NPM while archiving to avoid SQLite writes during backup.
# If downtime is not acceptable, use the SQLite .backup API with sqlite3.
#
# Usage:
#   ./backup/proxy-backup.sh [--dry-run]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
source "${SCRIPT_DIR}/lib/common.sh"

# Consumed by backup_init.
# shellcheck disable=SC2034
{
  STACK_NAME="proxy"
  STACK_DATA_VAR="PROXY_DATA_DIR"
  ARCHIVE_PREFIX="proxy-backup"
  PRUNE_PATTERN='^(proxy-backup|npm-backup)-'
}

backup_init "$@"
backup_require_commands docker

backup_log "Starting proxy backup: ${BACKUP_S3_BUCKET}/${ARCHIVE_NAME}"

# Install restart trap before stopping the stack. If `compose down` fails,
# `set -e` exits and the trap restores NPM; a trap installed after `down`
# would never be reached on that failure path.
trap 'backup_run backup_compose up -d; rm -rf "${TMP_DIR}"' EXIT

# Stop the stack so SQLite is in a clean state (no in-flight writes).
backup_log "Stopping proxy stack"
backup_run backup_compose down

backup_create_archive

# Restart before upload so downtime is limited to archive creation.
backup_log "Restarting proxy stack"
backup_run backup_compose up -d
trap 'rm -rf "${TMP_DIR}"' EXIT

backup_upload

rm -rf "${TMP_DIR}"
trap - EXIT

backup_prune "${PRUNE_PATTERN}"

backup_log "Backup complete: ${ARCHIVE_NAME}"
