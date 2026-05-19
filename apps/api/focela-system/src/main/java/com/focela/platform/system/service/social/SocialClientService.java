package com.focela.platform.system.service.social;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.social.dto.client.SocialClientPageRequest;
import com.focela.platform.system.controller.admin.social.dto.client.SocialClientSaveRequest;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import com.focela.platform.system.domain.entity.social.SocialClientEntity;
import jakarta.validation.Valid;
import me.zhyd.oauth.model.AuthUser;

import java.util.List;

/**
 * Social client service interface
 */
public interface SocialClientService {

    /**
     * Returns the OAuth2 authorization URL for the given social platform.
     *
     * @param socialType  social platform type {@link SocialTypeEnum}
     * @param userType    user type
     * @param redirectUri redirect URL after authorization
     * @return authorization URL
     */
    String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri);

    /**
     * Exchanges the OAuth2 callback code for an authenticated user.
     *
     * @param socialType social platform type
     * @param userType   user type
     * @param code       authorization code
     * @param state      state parameter
     * @return authenticated user
     */
    AuthUser getAuthUser(Integer socialType, Integer userType, String code, String state);

    // =================== Client management ===================

    Long createSocialClient(@Valid SocialClientSaveRequest createRequest);

    void updateSocialClient(@Valid SocialClientSaveRequest updateRequest);

    void deleteSocialClient(Long id);

    void deleteSocialClientList(List<Long> ids);

    SocialClientEntity getSocialClient(Long id);

    PageResult<SocialClientEntity> getSocialClientPage(SocialClientPageRequest pageRequest);

}
