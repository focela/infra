#!/usr/bin/env bash
# Backup for WireGuard keys and peer configs.
#
# Usage:
#   ./backup/wireguard-backup.sh [--dry-run]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
source "${SCRIPT_DIR}/lib/common.sh"

# Consumed by backup_init.
# shellcheck disable=SC2034
{
  STACK_NAME="wireguard"
  STACK_DATA_VAR="WIREGUARD_DATA_DIR"
  ARCHIVE_PREFIX="wireguard-backup"
  PRUNE_PATTERN='^wireguard-backup-'
}

backup_init "$@"

trap 'rm -rf "${TMP_DIR}"' EXIT

backup_log "Starting WireGuard backup: ${BACKUP_S3_BUCKET}/${ARCHIVE_NAME}"

backup_create_archive
backup_upload
backup_prune "${PRUNE_PATTERN}"

backup_log "Backup complete: ${ARCHIVE_NAME}"
