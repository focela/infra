package com.focela.platform.module.system.api.social;

import com.focela.platform.module.system.enums.social.SocialTypeEnum;

/**
 * Social client API interface
 */
public interface SocialClientApi {

    /**
     * Returns the OAuth2 authorization URL for the given social platform.
     *
     * @param socialType  social platform type {@link SocialTypeEnum}
     * @param userType    user type
     * @param redirectUri redirect URL after authorization
     * @return authorization URL
     */
    String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri);

}
