package com.focela.platform.system.controller.admin.oauth2;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenAccessTokenResponse;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenAuthorizeInfoResponse;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenCheckTokenResponse;
import com.focela.platform.system.converter.oauth2.OAuth2OpenConverter;
import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ApproveEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.enums.oauth2.OAuth2GrantTypeEnum;
import com.focela.platform.system.service.oauth2.OAuth2ApproveService;
import com.focela.platform.system.service.oauth2.OAuth2ClientService;
import com.focela.platform.system.service.oauth2.OAuth2GrantService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import com.focela.platform.system.config.oauth2.OAuth2Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception0;
import static com.focela.platform.common.model.CommonResult.success;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;
import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

/**
 * Mainly intended for external application calls
 *
 * Generally, the admin backend's /system-api/* is not exposed directly to external applications, mainly because the data and interfaces external applications can access are limited, and the admin backend's RBAC cannot control them well.
 * Many open platforms have an independent set of OpenAPIs; in [this system] that corresponds to creating a new open package under Controller, implementing /open-api/* interfaces, and controlling access via scope.
 * In addition, if a company has multiple admin backends, the access tokens produced by their respective client_id values cannot interoperate, i.e., they cannot access each other's system API interfaces, until trust authorization is established between the two client_id values.
 *
 * Considering that [this system] does not want to be overly complex for now, by default after obtaining an access token, all /system-api/* interfaces of [this system]'s admin backend can be accessed, unless scope control is added manually.
 * For an example of scope usage, see the {@link OAuth2UserController} class
 */
@Tag(name = "Admin - OAuth2 authorization")
@RestController
@RequestMapping("/system/oauth2")
@Validated
@Slf4j
@RequiredArgsConstructor
public class OAuth2OpenController {

    private final OAuth2GrantService oauth2GrantService;
    private final OAuth2ClientService oauth2ClientService;
    private final OAuth2ApproveService oauth2ApproveService;
    private final OAuth2TokenService oauth2TokenService;

    /**
     * Corresponds to the postAccessToken method of Spring Security OAuth's TokenEndpoint class
     *
     * authorization_code mode: code + redirectUri + state parameters
     * password mode: username + password + scope parameters
     * refresh_token mode: refreshToken parameter
     * client_credentials mode: scope parameter
     * implicit mode: not supported
     *
     * Note that client_id + client_secret parameters must be provided by default
     */
    @PostMapping("/token")
    @PermitAll
    @Operation(summary = "get access token", description = "For code/implicit grant; called by sso.vue on fetch")
    @Parameters({
            @Parameter(name = "grant_type", required = true, description = "authorize type", example = "code"),
            @Parameter(name = "code", description = "Scope", example = "userinfo.read"),
            @Parameter(name = "redirect_uri", description = "Redirect URI", example = "https://www.example.com"),
            @Parameter(name = "state", description = "Status", example = "1"),
            @Parameter(name = "username", example = "focela_alternate"),
            @Parameter(name = "password", example = "cai"), // multiple values separated by spaces
            @Parameter(name = "scope", example = "user_info"),
            @Parameter(name = "refresh_token", example = "123424233"),
    })
    @SuppressWarnings("EnhancedSwitchMigration")
    public CommonResult<OAuth2OpenAccessTokenResponse> postAccessToken(HttpServletRequest request,
                                                                     @RequestParam("grant_type") String grantType,
                                                                     @RequestParam(value = "code", required = false) String code, // authorization_code mode
                                                                     @RequestParam(value = "redirect_uri", required = false) String redirectUri, // authorization_code mode
                                                                     @RequestParam(value = "state", required = false) String state, // authorization_code mode
                                                                     @RequestParam(value = "username", required = false) String username, // password mode
                                                                     @RequestParam(value = "password", required = false) String password, // password mode
                                                                     @RequestParam(value = "scope", required = false) String scope, // password mode
                                                                     @RequestParam(value = "refresh_token", required = false) String refreshToken) { // refresh mode
        List<String> scopes = OAuth2Utils.buildScopes(scope);
        // 1.1 Validate grant type
        OAuth2GrantTypeEnum grantTypeEnum = OAuth2GrantTypeEnum.getByGrantType(grantType);
        if (grantTypeEnum == null) {
            throw exception0(BAD_REQUEST.getCode(), StrUtil.format("Unknown grant type({})", grantType));
        }
        if (grantTypeEnum == OAuth2GrantTypeEnum.IMPLICIT) {
            throw exception0(BAD_REQUEST.getCode(), "Token endpoint does not support implicit grant mode");
        }

        // 1.2 Validate client
        String[] clientIdAndSecret = obtainBasicAuthorization(request);
        OAuth2ClientEntity client = oauth2ClientService.validateOAuthClientFromCache(clientIdAndSecret[0], clientIdAndSecret[1],
                grantType, scopes, redirectUri);

        // 2. Obtain access token based on grant mode
        OAuth2AccessTokenEntity accessTokenEntity;
        switch (grantTypeEnum) {
            case AUTHORIZATION_CODE:
                accessTokenEntity = oauth2GrantService.grantAuthorizationCodeForAccessToken(client.getClientId(), code, redirectUri, state);
                break;
            case PASSWORD:
                accessTokenEntity = oauth2GrantService.grantPassword(username, password, client.getClientId(), scopes);
                break;
            case CLIENT_CREDENTIALS:
                accessTokenEntity = oauth2GrantService.grantClientCredentials(client.getClientId(), scopes);
                break;
            case REFRESH_TOKEN:
                accessTokenEntity = oauth2GrantService.grantRefreshToken(refreshToken, client.getClientId());
                break;
            default:
                throw new IllegalArgumentException("unknown authorize type:" + grantType);
        }
        Assert.notNull(accessTokenEntity, "access token must not be blank"); // defensive check
        return success(OAuth2OpenConverter.INSTANCE.convert(accessTokenEntity));
    }

    @DeleteMapping("/token")
    @PermitAll
    @Operation(summary = "Delete access token")
    @Parameter(name = "token", required = true, description = "Access token", example = "biu")
    public CommonResult<Boolean> revokeToken(HttpServletRequest request,
                                             @RequestParam("token") String token) {
        // Validate client
        String[] clientIdAndSecret = obtainBasicAuthorization(request);
        OAuth2ClientEntity client = oauth2ClientService.validateOAuthClientFromCache(clientIdAndSecret[0], clientIdAndSecret[1],
                null, null, null);

        // Delete access token
        return success(oauth2GrantService.revokeToken(client.getClientId(), token));
    }

    /**
     * Corresponds to the checkToken method of Spring Security OAuth's CheckTokenEndpoint class
     */
    @PostMapping("/check-token")
    @PermitAll
    @Operation(summary = "validate access token")
    @Parameter(name = "token", required = true, description = "Access token", example = "biu")
    public CommonResult<OAuth2OpenCheckTokenResponse> checkToken(HttpServletRequest request,
                                                               @RequestParam("token") String token) {
        // Validate client
        String[] clientIdAndSecret = obtainBasicAuthorization(request);
        oauth2ClientService.validateOAuthClientFromCache(clientIdAndSecret[0], clientIdAndSecret[1],
                null, null, null);

        // Validate token
        OAuth2AccessTokenEntity accessTokenEntity = oauth2TokenService.checkAccessToken(token);
        Assert.notNull(accessTokenEntity, "access token must not be blank"); // defensive check
        return success(OAuth2OpenConverter.INSTANCE.convertToCheckTokenResponse(accessTokenEntity));
    }

    /**
     * Corresponds to the authorize method of Spring Security OAuth's AuthorizationEndpoint class
     */
    @GetMapping("/authorize")
    @Operation(summary = "get authorize info", description = "For code/implicit grant; called by sso.vue on fetch")
    @Parameter(name = "clientId", required = true, description = "Client ID", example = "focela_alternate")
    public CommonResult<OAuth2OpenAuthorizeInfoResponse> authorize(@RequestParam("clientId") String clientId) {
        // 0. Validate that the user is logged in. Implemented via Spring Security

        // 1. Get Client info
        OAuth2ClientEntity client = oauth2ClientService.validateOAuthClientFromCache(clientId);
        // 2. Get info about scopes the user has already approved
        List<OAuth2ApproveEntity> approves = oauth2ApproveService.getApproveList(getLoginUserId(), getUserType(), clientId);
        // Assemble and return
        return success(OAuth2OpenConverter.INSTANCE.convert(client, approves));
    }

    /**
     * Corresponds to the approveOrDeny method of Spring Security OAuth's AuthorizationEndpoint class
     *
     * Scenario 1: [Auto-approve autoApprove = true]
     *      Upon entering the sso.vue page, this endpoint is called; the user has previously authorized this application, or the OAuth2Client supports auto-approval for this scope
     * Scenario 2: [Manual approval autoApprove = false]
     *      On the sso.vue page, after the user selects the scope authorization range, this endpoint is called to authorize. In this case, approved is true or false
     *
     * Because of the frontend/backend separation, Axios cannot handle 302 redirects well, so this differs slightly from Spring Security OAuth: the result is the redirect URL, and the rest is handled by the frontend
     */
    @PostMapping("/authorize")
    @Operation(summary = "apply authorization", description = "For code/implicit grant; called by sso.vue on submit")
    @Parameters({
            @Parameter(name = "response_type", required = true, description = "response type", example = "code"),
            @Parameter(name = "client_id", required = true, description = "Client ID", example = "focela_alternate"),
            @Parameter(name = "scope", description = "Scope", example = "userinfo.read"), // Uses Map<String, Boolean> format; Spring MVC does not currently support receiving parameters this way
            @Parameter(name = "redirect_uri", required = true, description = "Redirect URI", example = "https://www.example.com"),
            @Parameter(name = "auto_approve", required = true, description = "user accepted", example = "true"),
            @Parameter(name = "state", example = "1")
    })
    public CommonResult<String> approveOrDeny(@RequestParam("response_type") String responseType,
                                              @RequestParam("client_id") String clientId,
                                              @RequestParam(value = "scope", required = false) String scope,
                                              @RequestParam("redirect_uri") String redirectUri,
                                              @RequestParam(value = "auto_approve") Boolean autoApprove,
                                              @RequestParam(value = "state", required = false) String state) {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> scopes = JsonUtils.parseObject(scope, Map.class);
        scopes = ObjectUtil.defaultIfNull(scopes, Collections.emptyMap());
        // 0. Validate that the user is logged in. Implemented via Spring Security

        // 1.1 Validate that responseType matches either code or token
        OAuth2GrantTypeEnum grantTypeEnum = getGrantTypeEnum(responseType);
        // 1.2 Validate that the redirectUri domain is legal + validate that scope is within the Client's authorized scope
        OAuth2ClientEntity client = oauth2ClientService.validateOAuthClientFromCache(clientId, null,
                grantTypeEnum.getGrantType(), scopes.keySet(), redirectUri);

        // 2.1 If approved is null, scenario 1 applies
        if (Boolean.TRUE.equals(autoApprove)) {
            // If auto-approval fails, return an empty url; the frontend will not redirect
            if (!oauth2ApproveService.checkForPreApproval(getLoginUserId(), getUserType(), clientId, scopes.keySet())) {
                return success(null);
            }
        } else { // 2.2 If approved is non-null, scenario 2 applies
            // If validation fails, redirect to an error link
            if (!oauth2ApproveService.updateAfterApproval(getLoginUserId(), getUserType(), clientId, scopes)) {
                return success(OAuth2Utils.buildUnsuccessfulRedirect(redirectUri, responseType, state,
                        "access_denied", "User denied access"));
            }
        }

        // 3.1 If code authorization_code mode, issue the code authorization code and redirect
        List<String> approveScopes = convertList(scopes.entrySet(), Map.Entry::getKey, Map.Entry::getValue);
        if (grantTypeEnum == OAuth2GrantTypeEnum.AUTHORIZATION_CODE) {
            return success(getAuthorizationCodeRedirect(getLoginUserId(), client, approveScopes, redirectUri, state));
        }
        // 3.2 If token then implicit simplified mode: send the access token and redirect
        return success(getImplicitGrantRedirect(getLoginUserId(), client, approveScopes, redirectUri, state));
    }

    private static OAuth2GrantTypeEnum getGrantTypeEnum(String responseType) {
        if (StrUtil.equals(responseType, "code")) {
            return OAuth2GrantTypeEnum.AUTHORIZATION_CODE;
        }
        if (StrUtil.equalsAny(responseType, "token")) {
            return OAuth2GrantTypeEnum.IMPLICIT;
        }
        throw exception0(BAD_REQUEST.getCode(), "response_type parameter value only allows code and token");
    }

    private String getImplicitGrantRedirect(Long userId, OAuth2ClientEntity client,
                                            List<String> scopes, String redirectUri, String state) {
        // 1. Create access token
        OAuth2AccessTokenEntity accessTokenEntity = oauth2GrantService.grantImplicit(userId, getUserType(), client.getClientId(), scopes);
        Assert.notNull(accessTokenEntity, "access token must not be blank"); // defensive check
        // 2. Assemble redirect URL
        // noinspection unchecked
        return OAuth2Utils.buildImplicitRedirectUri(redirectUri, accessTokenEntity.getAccessToken(), state, accessTokenEntity.getExpiresTime(),
                scopes, JsonUtils.parseObject(client.getAdditionalInformation(), Map.class));
    }

    private String getAuthorizationCodeRedirect(Long userId, OAuth2ClientEntity client,
                                                List<String> scopes, String redirectUri, String state) {
        // 1. Create code authorization code
        String authorizationCode = oauth2GrantService.grantAuthorizationCodeForCode(userId, getUserType(), client.getClientId(), scopes,
                redirectUri, state);
        // 2. Assemble redirect URL
        return OAuth2Utils.buildAuthorizationCodeRedirectUri(redirectUri, authorizationCode, state);
    }

    private Integer getUserType() {
        return UserTypeEnum.ADMIN.getValue();
    }

    private String[] obtainBasicAuthorization(HttpServletRequest request) {
        String[] clientIdAndSecret = HttpUtils.obtainBasicAuthorization(request);
        if (ArrayUtil.isEmpty(clientIdAndSecret) || clientIdAndSecret.length != 2) {
            throw exception0(BAD_REQUEST.getCode(), "client_id or client_secret not properly provided");
        }
        return clientIdAndSecret;
    }

}
