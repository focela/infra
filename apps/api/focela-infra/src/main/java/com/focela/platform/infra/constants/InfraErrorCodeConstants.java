package com.focela.platform.infra.constants;

import com.focela.platform.common.exception.ErrorCode;

/**
 * Infra error code enum class
 *
 * infra system, uses 1-001-000-000 segment
 */
public interface InfraErrorCodeConstants {

    // ========== Config 1-001-000-000 ==========
    ErrorCode CONFIG_NOT_EXISTS = new ErrorCode(1_001_000_001, "Config does not exist");
    ErrorCode CONFIG_KEY_DUPLICATE = new ErrorCode(1_001_000_002, "Config key is duplicated");
    ErrorCode CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE = new ErrorCode(1_001_000_003, "Cannot delete config of system built-in type");
    ErrorCode CONFIG_GET_VALUE_ERROR_IF_VISIBLE = new ErrorCode(1_001_000_004, "Failed to get config, reason: not allowed to get invisible config");

    // ========== Scheduled job 1-001-001-000 ==========
    ErrorCode JOB_NOT_EXISTS = new ErrorCode(1_001_001_000, "Scheduled job does not exist");
    ErrorCode JOB_HANDLER_EXISTS = new ErrorCode(1_001_001_001, "Scheduled job handler already exists");
    ErrorCode JOB_CHANGE_STATUS_INVALID = new ErrorCode(1_001_001_002, "Only allowed to change to enabled or disabled status");
    ErrorCode JOB_CHANGE_STATUS_EQUALS = new ErrorCode(1_001_001_003, "Scheduled job is already in this status, no update needed");
    ErrorCode JOB_UPDATE_ONLY_NORMAL_STATUS = new ErrorCode(1_001_001_004, "Only jobs in enabled status can be updated");
    ErrorCode JOB_CRON_EXPRESSION_VALID = new ErrorCode(1_001_001_005, "CRON expression is invalid");
    ErrorCode JOB_HANDLER_BEAN_NOT_EXISTS = new ErrorCode(1_001_001_006, "Scheduled job handler Bean does not exist, note Bean name defaults to lowercase first letter");
    ErrorCode JOB_HANDLER_BEAN_TYPE_ERROR = new ErrorCode(1_001_001_007, "Scheduled job handler Bean type is incorrect, JobHandler interface is not implemented");

    // ========== API error log 1-001-002-000 ==========
    ErrorCode API_ERROR_LOG_NOT_FOUND = new ErrorCode(1_001_002_000, "API error log does not exist");
    ErrorCode API_ERROR_LOG_PROCESSED = new ErrorCode(1_001_002_001, "API error log has been processed");

    // ========= File related 1-001-003-000 =================
    ErrorCode FILE_PATH_EXISTS = new ErrorCode(1_001_003_000, "File path already exists");
    ErrorCode FILE_NOT_EXISTS = new ErrorCode(1_001_003_001, "File does not exist");
    ErrorCode FILE_IS_EMPTY = new ErrorCode(1_001_003_002, "File is empty");

    // ========== Code generator 1-001-004-000 ==========
    ErrorCode CODEGEN_TABLE_EXISTS = new ErrorCode(1_001_004_002, "Table definition already exists");
    ErrorCode CODEGEN_IMPORT_TABLE_NULL = new ErrorCode(1_001_004_001, "Imported table does not exist");
    ErrorCode CODEGEN_IMPORT_COLUMNS_NULL = new ErrorCode(1_001_004_002, "Imported columns do not exist");
    ErrorCode CODEGEN_TABLE_NOT_EXISTS = new ErrorCode(1_001_004_004, "Table definition does not exist");
    ErrorCode CODEGEN_COLUMN_NOT_EXISTS = new ErrorCode(1_001_004_005, "Column definition does not exist");
    ErrorCode CODEGEN_SYNC_COLUMNS_NULL = new ErrorCode(1_001_004_006, "Synced columns do not exist");
    ErrorCode CODEGEN_SYNC_NONE_CHANGE = new ErrorCode(1_001_004_007, "Sync failed, no changes detected");
    ErrorCode CODEGEN_TABLE_INFO_TABLE_COMMENT_IS_NULL = new ErrorCode(1_001_004_008, "Database table comment is not filled in");
    ErrorCode CODEGEN_TABLE_INFO_COLUMN_COMMENT_IS_NULL = new ErrorCode(1_001_004_009, "Database table column ({}) comment is not filled in");
    ErrorCode CODEGEN_MASTER_TABLE_NOT_EXISTS = new ErrorCode(1_001_004_010, "Master table (id={}) definition does not exist, please check");
    ErrorCode CODEGEN_SUB_COLUMN_NOT_EXISTS = new ErrorCode(1_001_004_011, "Sub-table column (id={}) does not exist, please check");
    ErrorCode CODEGEN_MASTER_GENERATION_FAIL_NO_SUB_TABLE = new ErrorCode(1_001_004_012, "Master table code generation failed, reason: it has no sub-tables");

    // ========== File config 1-001-006-000 ==========
    ErrorCode FILE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_006_000, "File config does not exist");
    ErrorCode FILE_CONFIG_DELETE_FAIL_MASTER = new ErrorCode(1_001_006_001, "This file config cannot be deleted, reason: it is the master config, deleting it will make file upload impossible");

    // ========== Data source config 1-001-007-000 ==========
    ErrorCode DATA_SOURCE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_007_000, "Data source config does not exist");
    ErrorCode DATA_SOURCE_CONFIG_NOT_OK = new ErrorCode(1_001_007_001, "Data source config is incorrect, unable to connect");

    // ========== Reference data 1-001-201-000 ==========
    ErrorCode REFERENCE_CONTACT_NOT_EXISTS = new ErrorCode(1_001_201_000, "Reference contact does not exist");
    ErrorCode REFERENCE_CATEGORY_NOT_EXISTS = new ErrorCode(1_001_201_001, "Reference category does not exist");
    ErrorCode REFERENCE_CATEGORY_HAS_CHILDREN = new ErrorCode(1_001_201_002, "Child reference categories exist, cannot delete");
    ErrorCode REFERENCE_CATEGORY_PARENT_NOT_EXISTS = new ErrorCode(1_001_201_003, "Parent reference category does not exist");
    ErrorCode REFERENCE_CATEGORY_PARENT_ERROR = new ErrorCode(1_001_201_004, "Cannot set itself as parent reference category");
    ErrorCode REFERENCE_CATEGORY_NAME_DUPLICATE = new ErrorCode(1_001_201_005, "A reference category with this name already exists");
    ErrorCode REFERENCE_CATEGORY_PARENT_IS_CHILD = new ErrorCode(1_001_201_006, "Cannot set its own child reference category as parent reference category");
    ErrorCode REFERENCE_USER_NOT_EXISTS = new ErrorCode(1_001_201_007, "Reference user does not exist");
    ErrorCode REFERENCE_COURSE_NOT_EXISTS = new ErrorCode(1_001_201_008, "Reference course does not exist");
    ErrorCode REFERENCE_GRADE_NOT_EXISTS = new ErrorCode(1_001_201_009, "Reference grade does not exist");
    ErrorCode REFERENCE_GRADE_EXISTS = new ErrorCode(1_001_201_010, "Reference grade already exists");

}
