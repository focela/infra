package com.focela.platform.system.constants;

import com.focela.platform.common.exception.ErrorCode;

/**
 * System error code enumeration
 *
 * system module uses the 1-002-000-000 segment
 */
public interface ErrorCodeConstants {

    // ========== AUTH module 1-002-000-000 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(1_002_000_000, "Login failed, username or password is incorrect");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(1_002_000_001, "Login failed, account is disabled");
    ErrorCode AUTH_LOGIN_CAPTCHA_CODE_ERROR = new ErrorCode(1_002_000_004, "Captcha is incorrect, reason: {}");
    ErrorCode AUTH_THIRD_LOGIN_NOT_BIND = new ErrorCode(1_002_000_005, "Account is not bound, binding is required");
    ErrorCode AUTH_MOBILE_NOT_EXISTS = new ErrorCode(1_002_000_007, "Mobile number does not exist");
    ErrorCode AUTH_REGISTER_CAPTCHA_CODE_ERROR = new ErrorCode(1_002_000_008, "Captcha is incorrect, reason: {}");

    // ========== Menu module 1-002-001-000 ==========
    ErrorCode MENU_NAME_DUPLICATE = new ErrorCode(1_002_001_000, "A menu with this name already exists");
    ErrorCode MENU_PARENT_NOT_EXISTS = new ErrorCode(1_002_001_001, "Parent menu does not exist");
    ErrorCode MENU_PARENT_ERROR = new ErrorCode(1_002_001_002, "Cannot set itself as the parent menu");
    ErrorCode MENU_NOT_EXISTS = new ErrorCode(1_002_001_003, "Menu does not exist");
    ErrorCode MENU_EXISTS_CHILDREN = new ErrorCode(1_002_001_004, "Child menus exist, cannot delete");
    ErrorCode MENU_PARENT_NOT_DIR_OR_MENU = new ErrorCode(1_002_001_005, "The parent menu type must be directory or menu");
    ErrorCode MENU_COMPONENT_NAME_DUPLICATE = new ErrorCode(1_002_001_006, "A menu with this component name already exists");

    // ========== Role module 1-002-002-000 ==========
    ErrorCode ROLE_NOT_EXISTS = new ErrorCode(1_002_002_000, "Role does not exist");
    ErrorCode ROLE_NAME_DUPLICATE = new ErrorCode(1_002_002_001, "A role with the name [{}] already exists");
    ErrorCode ROLE_CODE_DUPLICATE = new ErrorCode(1_002_002_002, "A role with the code [{}] already exists");
    ErrorCode ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE = new ErrorCode(1_002_002_003, "Cannot operate on a system built-in role");
    ErrorCode ROLE_IS_DISABLE = new ErrorCode(1_002_002_004, "The role with the name [{}] has been disabled");
    ErrorCode ROLE_ADMIN_CODE_ERROR = new ErrorCode(1_002_002_005, "The code [{}] cannot be used");

    // ========== User module 1-002-003-000 ==========
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1_002_003_000, "User account already exists");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(1_002_003_001, "Mobile number already exists");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1_002_003_002, "Email already exists");
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_002_003_003, "User does not exist");
    ErrorCode USER_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_002_003_004, "Imported user data must not be empty!");
    ErrorCode USER_PASSWORD_FAILED = new ErrorCode(1_002_003_005, "User password verification failed");
    ErrorCode USER_IS_DISABLE = new ErrorCode(1_002_003_006, "The user with the name [{}] has been disabled");
    ErrorCode USER_COUNT_MAX = new ErrorCode(1_002_003_008, "Failed to create user, reason: exceeded maximum tenant quota ({})!");
    ErrorCode USER_IMPORT_INIT_PASSWORD = new ErrorCode(1_002_003_009, "Initial password must not be empty");
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(1_002_003_010, "This mobile number has not been registered");
    ErrorCode USER_REGISTER_DISABLED = new ErrorCode(1_002_003_011, "Registration is disabled");

    // ========== Department module 1-002-004-000 ==========
    ErrorCode DEPT_NAME_DUPLICATE = new ErrorCode(1_002_004_000, "A department with this name already exists");
    ErrorCode DEPT_PARENT_NOT_EXITS = new ErrorCode(1_002_004_001,"Parent department does not exist");
    ErrorCode DEPT_NOT_FOUND = new ErrorCode(1_002_004_002, "Current department does not exist");
    ErrorCode DEPT_EXITS_CHILDREN = new ErrorCode(1_002_004_003, "Child departments exist, cannot delete");
    ErrorCode DEPT_PARENT_ERROR = new ErrorCode(1_002_004_004, "Cannot set itself as the parent department");
    ErrorCode DEPT_NOT_ENABLE = new ErrorCode(1_002_004_006, "Department ({}) is not enabled, cannot be selected");
    ErrorCode DEPT_PARENT_IS_CHILD = new ErrorCode(1_002_004_007, "Cannot set its own child department as the parent department");

    // ========== Post module 1-002-005-000 ==========
    ErrorCode POST_NOT_FOUND = new ErrorCode(1_002_005_000, "Current post does not exist");
    ErrorCode POST_NOT_ENABLE = new ErrorCode(1_002_005_001, "Post ({}) is not enabled, cannot be selected");
    ErrorCode POST_NAME_DUPLICATE = new ErrorCode(1_002_005_002, "A post with this name already exists");
    ErrorCode POST_CODE_DUPLICATE = new ErrorCode(1_002_005_003, "A post with this code already exists");

    // ========== Dictionary type 1-002-006-000 ==========
    ErrorCode DICT_TYPE_NOT_EXISTS = new ErrorCode(1_002_006_001, "Current dictionary type does not exist");
    ErrorCode DICT_TYPE_NOT_ENABLE = new ErrorCode(1_002_006_002, "Dictionary type is not enabled, cannot be selected");
    ErrorCode DICT_TYPE_NAME_DUPLICATE = new ErrorCode(1_002_006_003, "A dictionary type with this name already exists");
    ErrorCode DICT_TYPE_TYPE_DUPLICATE = new ErrorCode(1_002_006_004, "A dictionary type with this type already exists");
    ErrorCode DICT_TYPE_HAS_CHILDREN = new ErrorCode(1_002_006_005, "Cannot delete, this dictionary type still has dictionary data");

    // ========== Dictionary data 1-002-007-000 ==========
    ErrorCode DICT_DATA_NOT_EXISTS = new ErrorCode(1_002_007_001, "Current dictionary data does not exist");
    ErrorCode DICT_DATA_NOT_ENABLE = new ErrorCode(1_002_007_002, "Dictionary data ({}) is not enabled, cannot be selected");
    ErrorCode DICT_DATA_VALUE_DUPLICATE = new ErrorCode(1_002_007_003, "A dictionary data with this value already exists");

    // ========== Notice 1-002-008-000 ==========
    ErrorCode NOTICE_NOT_FOUND = new ErrorCode(1_002_008_001, "Current notice does not exist");

    // ========== SMS channel 1-002-011-000 ==========
    ErrorCode SMS_CHANNEL_NOT_EXISTS = new ErrorCode(1_002_011_000, "SMS channel does not exist");
    ErrorCode SMS_CHANNEL_DISABLE = new ErrorCode(1_002_011_001, "SMS channel is not enabled, cannot be selected");
    ErrorCode SMS_CHANNEL_HAS_CHILDREN = new ErrorCode(1_002_011_002, "Cannot delete, this SMS channel still has SMS templates");

    // ========== SMS template 1-002-012-000 ==========
    ErrorCode SMS_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_012_000, "SMS template does not exist");
    ErrorCode SMS_TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_002_012_001, "An SMS template with the code [{}] already exists");
    ErrorCode SMS_TEMPLATE_API_ERROR = new ErrorCode(1_002_012_002, "SMS API template call failed, reason: {}");
    ErrorCode SMS_TEMPLATE_API_AUDIT_CHECKING = new ErrorCode(1_002_012_003, "SMS API template cannot be used, reason: audit in progress");
    ErrorCode SMS_TEMPLATE_API_AUDIT_FAIL = new ErrorCode(1_002_012_004, "SMS API template cannot be used, reason: audit not approved, {}");
    ErrorCode SMS_TEMPLATE_API_NOT_FOUND = new ErrorCode(1_002_012_005, "SMS API template cannot be used, reason: template does not exist");

    // ========== SMS sending 1-002-013-000 ==========
    ErrorCode SMS_SEND_MOBILE_NOT_EXISTS = new ErrorCode(1_002_013_000, "Mobile number does not exist");
    ErrorCode SMS_SEND_MOBILE_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_013_001, "Template parameter ({}) is missing");
    ErrorCode SMS_SEND_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_013_002, "SMS template does not exist");

    // ========== SMS verification code 1-002-014-000 ==========
    ErrorCode SMS_CODE_NOT_FOUND = new ErrorCode(1_002_014_000, "Verification code does not exist");
    ErrorCode SMS_CODE_EXPIRED = new ErrorCode(1_002_014_001, "Verification code has expired");
    ErrorCode SMS_CODE_USED = new ErrorCode(1_002_014_002, "Verification code has been used");
    ErrorCode SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY = new ErrorCode(1_002_014_004, "Exceeded daily SMS sending limit");
    ErrorCode SMS_CODE_SEND_TOO_FAST = new ErrorCode(1_002_014_005, "SMS sending is too frequent");

    // ========== Tenant information 1-002-015-000 ==========
    ErrorCode TENANT_NOT_EXISTS = new ErrorCode(1_002_015_000, "Tenant does not exist");
    ErrorCode TENANT_DISABLE = new ErrorCode(1_002_015_001, "The tenant with the name [{}] has been disabled");
    ErrorCode TENANT_EXPIRE = new ErrorCode(1_002_015_002, "The tenant with the name [{}] has expired");
    ErrorCode TENANT_CAN_NOT_UPDATE_SYSTEM = new ErrorCode(1_002_015_003, "The system tenant cannot be modified, deleted, or operated on!");
    ErrorCode TENANT_NAME_DUPLICATE = new ErrorCode(1_002_015_004, "A tenant with the name [{}] already exists");
    ErrorCode TENANT_WEBSITE_DUPLICATE = new ErrorCode(1_002_015_005, "A tenant with the domain [{}] already exists");

    // ========== Tenant package 1-002-016-000 ==========
    ErrorCode TENANT_PACKAGE_NOT_EXISTS = new ErrorCode(1_002_016_000, "Tenant package does not exist");
    ErrorCode TENANT_PACKAGE_USED = new ErrorCode(1_002_016_001, "The tenant is using this package, please reassign the tenant package before trying to delete");
    ErrorCode TENANT_PACKAGE_DISABLE = new ErrorCode(1_002_016_002, "The tenant package with the name [{}] has been disabled");
    ErrorCode TENANT_PACKAGE_NAME_DUPLICATE = new ErrorCode(1_002_016_003, "A tenant package with this name already exists");

    // ========== Social user 1-002-018-000 ==========
    ErrorCode SOCIAL_USER_AUTH_FAILURE = new ErrorCode(1_002_018_000, "Social authorization failed, reason: {}");
    ErrorCode SOCIAL_USER_NOT_FOUND = new ErrorCode(1_002_018_001, "Social authorization failed, the corresponding user was not found");

    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_PHONE_CODE_ERROR = new ErrorCode(1_002_018_200, "Failed to get mobile number");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_QRCODE_ERROR = new ErrorCode(1_002_018_201, "Failed to get mini program QR code");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_SUBSCRIBE_TEMPLATE_ERROR = new ErrorCode(1_002_018_202, "Failed to get mini program subscription message template");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_SUBSCRIBE_MESSAGE_ERROR = new ErrorCode(1_002_018_203, "Failed to send mini program subscription message");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_ORDER_UPLOAD_SHIPPING_INFO_ERROR = new ErrorCode(1_002_018_204, "Failed to upload WeChat mini program shipping information");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_ORDER_NOTIFY_CONFIRM_RECEIVE_ERROR = new ErrorCode(1_002_018_205, "Failed to upload WeChat mini program order receipt information");
    ErrorCode SOCIAL_CLIENT_NOT_EXISTS = new ErrorCode(1_002_018_210, "Social client does not exist");
    ErrorCode SOCIAL_CLIENT_UNIQUE = new ErrorCode(1_002_018_211, "Social client configuration already exists");

    // ========== OAuth2 client 1-002-020-000 =========
    ErrorCode OAUTH2_CLIENT_NOT_EXISTS = new ErrorCode(1_002_020_000, "OAuth2 client does not exist");
    ErrorCode OAUTH2_CLIENT_EXISTS = new ErrorCode(1_002_020_001, "OAuth2 client ID already exists");
    ErrorCode OAUTH2_CLIENT_DISABLE = new ErrorCode(1_002_020_002, "OAuth2 client is disabled");
    ErrorCode OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS = new ErrorCode(1_002_020_003, "Authorization grant type is not supported");
    ErrorCode OAUTH2_CLIENT_SCOPE_OVER = new ErrorCode(1_002_020_004, "Authorization scope exceeds allowed range");
    ErrorCode OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH = new ErrorCode(1_002_020_005, "Invalid redirect_uri: {}");
    ErrorCode OAUTH2_CLIENT_CLIENT_SECRET_ERROR = new ErrorCode(1_002_020_006, "Invalid client_secret: {}");

    // ========== OAuth2 grant 1-002-021-000 =========
    ErrorCode OAUTH2_GRANT_CLIENT_ID_MISMATCH = new ErrorCode(1_002_021_000, "client_id mismatch");
    ErrorCode OAUTH2_GRANT_REDIRECT_URI_MISMATCH = new ErrorCode(1_002_021_001, "redirect_uri mismatch");
    ErrorCode OAUTH2_GRANT_STATE_MISMATCH = new ErrorCode(1_002_021_002, "state mismatch");

    // ========== OAuth2 code 1-002-022-000 =========
    ErrorCode OAUTH2_CODE_NOT_EXISTS = new ErrorCode(1_002_022_000, "code does not exist");
    ErrorCode OAUTH2_CODE_EXPIRE = new ErrorCode(1_002_022_001, "code has expired");

    // ========== Mail account 1-002-023-000 ==========
    ErrorCode MAIL_ACCOUNT_NOT_EXISTS = new ErrorCode(1_002_023_000, "Mail account does not exist");
    ErrorCode MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS = new ErrorCode(1_002_023_001, "Cannot delete, this mail account still has mail templates");

    // ========== Mail template 1-002-024-000 ==========
    ErrorCode MAIL_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_024_000, "Mail template does not exist");
    ErrorCode MAIL_TEMPLATE_CODE_EXISTS = new ErrorCode(1_002_024_001, "Mail template code({}) already exists");

    // ========== Mail sending 1-002-025-000 ==========
    ErrorCode MAIL_SEND_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_025_000, "Template parameter ({}) is missing");
    ErrorCode MAIL_SEND_MAIL_NOT_EXISTS = new ErrorCode(1_002_025_001, "Mailbox does not exist");

    // ========== Notify template 1-002-026-000 ==========
    ErrorCode NOTIFY_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_026_000, "Notify template does not exist");
    ErrorCode NOTIFY_TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_002_026_001, "A notify template with the code [{}] already exists");

    // ========== Notify template 1-002-027-000 ==========

    // ========== Notify sending 1-002-028-000 ==========
    ErrorCode NOTIFY_SEND_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_028_000, "Template parameter ({}) is missing");

}
