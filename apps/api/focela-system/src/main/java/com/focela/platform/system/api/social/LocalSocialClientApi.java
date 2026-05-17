package com.focela.platform.system.api.social;

import com.focela.platform.system.service.social.SocialClientService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;

/**
 * Social client API implementation (same-JVM delegate)
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalSocialClientApi implements SocialClientApi {

        private final SocialClientService socialClientService;

    @Override
    public String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri) {
        return socialClientService.getAuthorizeUrl(socialType, userType, redirectUri);
    }

}
