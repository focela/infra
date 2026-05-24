package com.focela.platform.system.arch;

import com.focela.platform.test.core.arch.ArchitectureRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Enforces the focela-system module's architectural conventions. See
 * {@code docs/MODULE_TEMPLATE.md} for the rationale.
 */
class SystemArchitectureTest {

    private static final List<String> LEGACY_CONTROLLER_PAYLOAD_IMPORTERS = List.of(
            "src/main/java/com/focela/platform/system/converter/auth/AuthConverter.java",
            "src/main/java/com/focela/platform/system/converter/oauth2/OAuth2OpenConverter.java",
            "src/main/java/com/focela/platform/system/converter/tenant/TenantConverter.java",
            "src/main/java/com/focela/platform/system/converter/user/UserConverter.java",
            "src/main/java/com/focela/platform/system/repository/mapper/department/DepartmentMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/department/PostMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/dictionary/DictionaryDataMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/dictionary/DictionaryTypeMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/logger/LoginLogMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/logger/OperateLogMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/mail/MailAccountMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/mail/MailLogMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/mail/MailTemplateMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/notice/NoticeMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/notify/NotifyMessageMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/notify/NotifyTemplateMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/oauth2/OAuth2AccessTokenMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/oauth2/OAuth2ClientMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/permission/MenuMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/permission/RoleMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/sms/SmsChannelMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/sms/SmsLogMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/sms/SmsTemplateMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/social/SocialClientMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/social/SocialUserMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/tenant/TenantMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/tenant/TenantPackageMapper.java",
            "src/main/java/com/focela/platform/system/repository/mapper/user/UserMapper.java",
            "src/main/java/com/focela/platform/system/service/auth/AuthService.java",
            "src/main/java/com/focela/platform/system/service/auth/DefaultAuthService.java",
            "src/main/java/com/focela/platform/system/service/department/DefaultDepartmentService.java",
            "src/main/java/com/focela/platform/system/service/department/DefaultPostService.java",
            "src/main/java/com/focela/platform/system/service/department/DepartmentService.java",
            "src/main/java/com/focela/platform/system/service/department/PostService.java",
            "src/main/java/com/focela/platform/system/service/dictionary/DefaultDictionaryDataService.java",
            "src/main/java/com/focela/platform/system/service/dictionary/DefaultDictionaryTypeService.java",
            "src/main/java/com/focela/platform/system/service/dictionary/DictionaryDataService.java",
            "src/main/java/com/focela/platform/system/service/dictionary/DictionaryTypeService.java",
            "src/main/java/com/focela/platform/system/service/logger/DefaultLoginLogService.java",
            "src/main/java/com/focela/platform/system/service/logger/DefaultOperateLogService.java",
            "src/main/java/com/focela/platform/system/service/logger/LoginLogService.java",
            "src/main/java/com/focela/platform/system/service/logger/OperateLogService.java",
            "src/main/java/com/focela/platform/system/service/mail/DefaultMailAccountService.java",
            "src/main/java/com/focela/platform/system/service/mail/DefaultMailLogService.java",
            "src/main/java/com/focela/platform/system/service/mail/DefaultMailTemplateService.java",
            "src/main/java/com/focela/platform/system/service/mail/MailAccountService.java",
            "src/main/java/com/focela/platform/system/service/mail/MailLogService.java",
            "src/main/java/com/focela/platform/system/service/mail/MailTemplateService.java",
            "src/main/java/com/focela/platform/system/service/notice/DefaultNoticeService.java",
            "src/main/java/com/focela/platform/system/service/notice/NoticeService.java",
            "src/main/java/com/focela/platform/system/service/notify/DefaultNotifyMessageService.java",
            "src/main/java/com/focela/platform/system/service/notify/DefaultNotifyTemplateService.java",
            "src/main/java/com/focela/platform/system/service/notify/NotifyMessageService.java",
            "src/main/java/com/focela/platform/system/service/notify/NotifyTemplateService.java",
            "src/main/java/com/focela/platform/system/service/oauth2/DefaultOAuth2ClientService.java",
            "src/main/java/com/focela/platform/system/service/oauth2/DefaultOAuth2TokenService.java",
            "src/main/java/com/focela/platform/system/service/oauth2/OAuth2ClientService.java",
            "src/main/java/com/focela/platform/system/service/oauth2/OAuth2TokenService.java",
            "src/main/java/com/focela/platform/system/service/permission/DefaultMenuService.java",
            "src/main/java/com/focela/platform/system/service/permission/DefaultRoleService.java",
            "src/main/java/com/focela/platform/system/service/permission/MenuService.java",
            "src/main/java/com/focela/platform/system/service/permission/RoleService.java",
            "src/main/java/com/focela/platform/system/service/sms/DefaultSmsChannelService.java",
            "src/main/java/com/focela/platform/system/service/sms/DefaultSmsLogService.java",
            "src/main/java/com/focela/platform/system/service/sms/DefaultSmsTemplateService.java",
            "src/main/java/com/focela/platform/system/service/sms/SmsChannelService.java",
            "src/main/java/com/focela/platform/system/service/sms/SmsLogService.java",
            "src/main/java/com/focela/platform/system/service/sms/SmsTemplateService.java",
            "src/main/java/com/focela/platform/system/service/social/DefaultSocialClientService.java",
            "src/main/java/com/focela/platform/system/service/social/DefaultSocialUserService.java",
            "src/main/java/com/focela/platform/system/service/social/SocialClientService.java",
            "src/main/java/com/focela/platform/system/service/social/SocialUserService.java",
            "src/main/java/com/focela/platform/system/service/tenant/DefaultTenantPackageService.java",
            "src/main/java/com/focela/platform/system/service/tenant/DefaultTenantService.java",
            "src/main/java/com/focela/platform/system/service/tenant/TenantPackageService.java",
            "src/main/java/com/focela/platform/system/service/tenant/TenantService.java",
            "src/main/java/com/focela/platform/system/service/user/DefaultUserService.java",
            "src/main/java/com/focela/platform/system/service/user/UserService.java"
    );

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = ArchitectureRules.importModule("com.focela.platform.system");
    }

    @Test
    void controllerLocation() {
        ArchitectureRules.CONTROLLER_RESIDES_IN_CONTROLLER_PACKAGE.check(classes);
    }

    @Test
    void entityLocation() {
        ArchitectureRules.ENTITY_RESIDES_IN_ENTITY_PACKAGE.check(classes);
    }

    @Test
    void mapperLocation() {
        ArchitectureRules.MAPPER_RESIDES_IN_REPOSITORY_MAPPER_PACKAGE.check(classes);
    }

    @Test
    void redisRepositoryLocation() {
        ArchitectureRules.REDIS_REPOSITORY_RESIDES_IN_REDIS_PACKAGE.check(classes);
    }

    @Test
    void configurationLocation() {
        ArchitectureRules.CONFIGURATION_RESIDES_IN_CONFIG_PACKAGE.check(classes);
    }

    @Test
    void constantsLocation() {
        ArchitectureRules.CONSTANTS_RESIDES_IN_CONSTANTS_PACKAGE.check(classes);
    }

    @Test
    void enumLocation() {
        ArchitectureRules.ENUM_RESIDES_IN_ENUMS_PACKAGE.check(classes);
    }

    @Test
    void controllerDoesNotUseRepository() {
        ArchitectureRules.CONTROLLER_DOES_NOT_USE_REPOSITORY.check(classes);
    }

    @Test
    void serviceDoesNotUseController() {
        ArchitectureRules.SERVICE_DOES_NOT_USE_CONTROLLER.check(classes);
    }

    @Test
    void repositoryDoesNotUseController() {
        ArchitectureRules.REPOSITORY_DOES_NOT_USE_CONTROLLER.check(classes);
    }

    @Test
    void repositoryDoesNotUseService() {
        ArchitectureRules.REPOSITORY_DOES_NOT_USE_SERVICE.check(classes);
    }

    @Test
    void controllerRequestSuffix() {
        ArchitectureRules.CONTROLLER_REQUEST_HAS_APPROVED_SUFFIX.check(classes);
    }

    @Test
    void controllerResponseSuffix() {
        ArchitectureRules.CONTROLLER_RESPONSE_HAS_APPROVED_SUFFIX.check(classes);
    }

    @Test
    void controllerRequestLocation() {
        ArchitectureRules.CONTROLLER_REQUEST_RESIDES_IN_REQUEST_PACKAGE.check(classes);
    }

    @Test
    void controllerResponseLocation() {
        ArchitectureRules.CONTROLLER_RESPONSE_RESIDES_IN_RESPONSE_PACKAGE.check(classes);
    }

    @Test
    void controllerDoesNotUseDtoPackage() {
        ArchitectureRules.CONTROLLER_DOES_NOT_USE_DTO_PACKAGE.check(classes);
    }

    @Test
    void controllerPayloadImportsAreNotExpandedOutsideControllerLayer() {
        ArchitectureRules.assertControllerPayloadImportsStayInAllowedFiles(
                "focela-system",
                LEGACY_CONTROLLER_PAYLOAD_IMPORTERS);
    }

    @Test
    void adminAndAppControllerImportsAreNotExpanded() {
        ArchitectureRules.assertAdminAppControllerImportsStayInAllowedFiles(
                "focela-system",
                "com.focela.platform.system",
                List.of());
    }

    @Test
    void mainJavaContainsOnlyJavaFiles() {
        ArchitectureRules.assertMainJavaContainsOnlyJavaFiles("focela-system");
    }

    @Test
    void legacyTestPrefixShouldNotBeUsed() {
        ArchitectureRules.assertTestMethodNamesDoNotUseLegacyPrefix("focela-system");
    }

    @Test
    void legacyAbbreviationLocationsAreAllowlisted() {
        ArchitectureRules.assertMainJavaLegacyAbbreviationsStayInAllowedFiles("focela-system", List.of(
                "src/main/java/com/focela/platform/system/api/department/DepartmentApi.java",
                "src/main/java/com/focela/platform/system/api/department/LocalDepartmentApi.java",
                "src/main/java/com/focela/platform/system/api/dictionary/LocalDictionaryDataApi.java",
                "src/main/java/com/focela/platform/system/api/permission/LocalPermissionApi.java",
                "src/main/java/com/focela/platform/system/api/user/UserApi.java",
                "src/main/java/com/focela/platform/system/api/user/dto/UserRpcResponse.java",
                "src/main/java/com/focela/platform/system/config/operation/DepartmentParseFunction.java",
                "src/main/java/com/focela/platform/system/controller/admin/auth/response/AuthPermissionInfoResponse.java",
                "src/main/java/com/focela/platform/system/controller/admin/dictionary/DictionaryDataController.java",
                "src/main/java/com/focela/platform/system/controller/admin/dictionary/DictionaryTypeController.java",
                "src/main/java/com/focela/platform/system/controller/admin/dictionary/request/data/DictionaryDataPageRequest.java",
                "src/main/java/com/focela/platform/system/controller/admin/dictionary/request/data/DictionaryDataSaveRequest.java",
                "src/main/java/com/focela/platform/system/controller/admin/dictionary/response/data/DictionaryDataResponse.java",
                "src/main/java/com/focela/platform/system/controller/admin/dictionary/response/data/DictionaryDataSimpleResponse.java",
                "src/main/java/com/focela/platform/system/controller/admin/oauth2/OAuth2UserController.java",
                "src/main/java/com/focela/platform/system/controller/admin/permission/request/PermissionAssignRoleDataScopeRequest.java",
                "src/main/java/com/focela/platform/system/controller/admin/permission/response/role/RoleResponse.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/UserController.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/UserProfileController.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/request/UserImportExcelRow.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/request/UserPageRequest.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/request/UserSaveRequest.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/response/UserResponse.java",
                "src/main/java/com/focela/platform/system/controller/admin/user/response/UserSimpleResponse.java",
                "src/main/java/com/focela/platform/system/controller/app/dictionary/AppDictionaryDataController.java",
                "src/main/java/com/focela/platform/system/controller/app/dictionary/response/AppDictionaryDataResponse.java",
                "src/main/java/com/focela/platform/system/converter/user/UserConverter.java",
                "src/main/java/com/focela/platform/system/domain/entity/dictionary/DictionaryDataEntity.java",
                "src/main/java/com/focela/platform/system/domain/entity/permission/RoleEntity.java",
                "src/main/java/com/focela/platform/system/domain/entity/user/UserEntity.java",
                "src/main/java/com/focela/platform/system/repository/mapper/dictionary/DictionaryDataMapper.java",
                "src/main/java/com/focela/platform/system/repository/mapper/user/UserMapper.java",
                "src/main/java/com/focela/platform/system/service/dictionary/DefaultDictionaryDataService.java",
                "src/main/java/com/focela/platform/system/service/dictionary/DefaultDictionaryTypeService.java",
                "src/main/java/com/focela/platform/system/service/dictionary/DictionaryDataService.java",
                "src/main/java/com/focela/platform/system/service/dictionary/DictionaryTypeService.java",
                "src/main/java/com/focela/platform/system/service/oauth2/DefaultOAuth2TokenService.java",
                "src/main/java/com/focela/platform/system/service/permission/DefaultPermissionService.java",
                "src/main/java/com/focela/platform/system/service/permission/DefaultRoleService.java",
                "src/main/java/com/focela/platform/system/service/permission/PermissionService.java",
                "src/main/java/com/focela/platform/system/service/permission/RoleService.java",
                "src/main/java/com/focela/platform/system/service/user/DefaultUserService.java",
                "src/main/java/com/focela/platform/system/service/user/UserService.java"
        ));
    }

    @Test
    void noLegacyNamingSuffixes() {
        ArchitectureRules.NO_LEGACY_NAMING_SUFFIXES.check(classes);
    }

    @Test
    void noLegacyPackageDependencies() {
        ArchitectureRules.NO_LEGACY_PACKAGE_DEPENDENCIES.check(classes);
    }

    @Test
    void systemDoesNotReachInfra() {
        ArchitectureRules.moduleDoesNotReachOtherModuleInternals(
                "com.focela.platform.system",
                "com.focela.platform.infra").check(classes);
    }

    @Test
    void systemSourceDoesNotImportInfraInternals() {
        ArchitectureRules.assertMainJavaDoesNotImportForbiddenPackages(
                "focela-system",
                List.of("com.focela.platform.infra."),
                List.of("com.focela.platform.infra.api."),
                List.of());
    }
}
