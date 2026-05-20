package com.focela.platform.system.constants;

import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;

/**
 * System Redis Key enum
 */
public interface RedisKeyConstants {

    /**
     * Cache of child department ID array for a given department
     * <p>
     * KEY format: dept_children_ids:{id}
     * VALUE type: String collection of child department IDs
     */
    String DEPARTMENT_CHILDREN_ID_LIST = "dept_children_ids";

    /**
     * Cache of role
     * <p>
     * KEY format: role:{id}
     * VALUE type: String role information
     */
    String ROLE = "role";

    /**
     * Cache of role IDs owned by a user
     * <p>
     * KEY format: user_role_ids:{userId}
     * VALUE type: String collection of role IDs
     */
    String USER_ROLE_ID_LIST = "user_role_ids";

    /**
     * Cache of role IDs owning a given menu
     * <p>
     * KEY format: user_role_ids:{menuId}
     * VALUE type: String collection of role IDs
     */
    String MENU_ROLE_ID_LIST = "menu_role_ids";

    /**
     * Cache of menu ID array for a given permission
     * <p>
     * KEY format: permission_menu_ids:{permission}
     * VALUE type: String menu ID array
     */
    String PERMISSION_MENU_ID_LIST = "permission_menu_ids";

    /**
     * Cache of OAuth2 client
     * <p>
     * KEY format: oauth_client:{id}
     * VALUE type: String client information
     */
    String OAUTH_CLIENT = "oauth_client";

    /**
     * Cache of access token
     * <p>
     * KEY format: oauth2_access_token:{token}
     * VALUE type: String access token information {@link OAuth2AccessTokenEntity}
     * <p>
     * Because the expiration time is dynamic, use RedisTemplate to operate
     */
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";

    /**
     * Cache of notification template
     * <p>
     * KEY format: notify_template:{code}
     * VALUE format: String template information
     */
    String NOTIFY_TEMPLATE = "notify_template";

    /**
     * Cache of email account
     * <p>
     * KEY format: mail_account:{id}
     * VALUE format: String account information
     */
    String MAIL_ACCOUNT = "mail_account";

    /**
     * Cache of email template
     * <p>
     * KEY format: mail_template:{code}
     * VALUE format: String template information
     */
    String MAIL_TEMPLATE = "mail_template";

    /**
     * Cache of SMS template
     * <p>
     * KEY format: sms_template:{id}
     * VALUE format: String template information
     */
    String SMS_TEMPLATE = "sms_template";

    /**
     * Cache of mini-program subscribe template
     *
     * KEY format: wxa_subscribe_template:{userType}
     * VALUE format: String template information
     */
    String WXA_SUBSCRIBE_TEMPLATE = "wxa_subscribe_template";

}
