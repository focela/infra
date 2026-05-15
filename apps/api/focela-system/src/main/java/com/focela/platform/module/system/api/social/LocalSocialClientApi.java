package com.focela.platform.module.system.api.social;

import com.focela.platform.module.system.service.social.SocialClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Social client API implementation (same-JVM delegate)
 */
@Service
@Validated
public class LocalSocialClientApi implements SocialClientApi {

    @Resource
    private SocialClientService socialClientService;

    @Override
    public String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri) {
        return socialClientService.getAuthorizeUrl(socialType, userType, redirectUri);
    }

}
