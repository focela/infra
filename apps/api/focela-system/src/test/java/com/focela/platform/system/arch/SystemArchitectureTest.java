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
    void systemDoesNotReachInfra() {
        ArchitectureRules.moduleDoesNotReachOtherModuleInternals(
                "com.focela.platform.system",
                "com.focela.platform.infra").check(classes);
    }
}
