package com.focela.platform.infra.arch;

import com.focela.platform.test.core.arch.ArchitectureRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Enforces the focela-infra module's architectural conventions. See
 * {@code docs/MODULE_TEMPLATE.md} for the rationale.
 */
class InfraArchitectureTest {

    private static final List<String> LEGACY_CONTROLLER_PAYLOAD_IMPORTERS = List.of(
            "src/main/java/com/focela/platform/infra/converter/config/ConfigConverter.java",
            "src/main/java/com/focela/platform/infra/converter/file/FileConfigConverter.java",
            "src/main/java/com/focela/platform/infra/converter/redis/RedisConverter.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/config/ConfigMapper.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/file/FileConfigMapper.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/file/FileMapper.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/job/JobLogMapper.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/job/JobMapper.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/logger/ApiAccessLogMapper.java",
            "src/main/java/com/focela/platform/infra/repository/mapper/logger/ApiErrorLogMapper.java",
            "src/main/java/com/focela/platform/infra/service/config/ConfigService.java",
            "src/main/java/com/focela/platform/infra/service/config/DefaultConfigService.java",
            "src/main/java/com/focela/platform/infra/service/database/DataSourceConfigService.java",
            "src/main/java/com/focela/platform/infra/service/database/DefaultDataSourceConfigService.java",
            "src/main/java/com/focela/platform/infra/service/file/DefaultFileConfigService.java",
            "src/main/java/com/focela/platform/infra/service/file/DefaultFileService.java",
            "src/main/java/com/focela/platform/infra/service/file/FileConfigService.java",
            "src/main/java/com/focela/platform/infra/service/file/FileService.java",
            "src/main/java/com/focela/platform/infra/service/job/DefaultJobLogService.java",
            "src/main/java/com/focela/platform/infra/service/job/DefaultJobService.java",
            "src/main/java/com/focela/platform/infra/service/job/JobLogService.java",
            "src/main/java/com/focela/platform/infra/service/job/JobService.java",
            "src/main/java/com/focela/platform/infra/service/logger/ApiAccessLogService.java",
            "src/main/java/com/focela/platform/infra/service/logger/ApiErrorLogService.java",
            "src/main/java/com/focela/platform/infra/service/logger/DefaultApiAccessLogService.java",
            "src/main/java/com/focela/platform/infra/service/logger/DefaultApiErrorLogService.java"
    );

    private static final List<String> LEGACY_ADMIN_APP_CONTROLLER_IMPORTERS = List.of(
            "src/main/java/com/focela/platform/infra/controller/app/file/AppFileController.java",
            "src/main/java/com/focela/platform/infra/controller/app/file/request/AppFileUploadRequest.java"
    );

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = ArchitectureRules.importModule("com.focela.platform.infra");
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
                "focela-infra",
                LEGACY_CONTROLLER_PAYLOAD_IMPORTERS);
    }

    @Test
    void adminAndAppControllerImportsAreNotExpanded() {
        ArchitectureRules.assertAdminAppControllerImportsStayInAllowedFiles(
                "focela-infra",
                "com.focela.platform.infra",
                LEGACY_ADMIN_APP_CONTROLLER_IMPORTERS);
    }

    @Test
    void mainJavaContainsOnlyJavaFiles() {
        ArchitectureRules.assertMainJavaContainsOnlyJavaFiles("focela-infra");
    }

    @Test
    void legacyTestPrefixShouldNotBeUsed() {
        ArchitectureRules.assertTestMethodNamesDoNotUseLegacyPrefix("focela-infra");
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
    void infraDoesNotReachSystem() {
        ArchitectureRules.moduleDoesNotReachOtherModuleInternals(
                "com.focela.platform.infra",
                "com.focela.platform.system").check(classes);
    }

    @Test
    void infraSourceDoesNotImportSystemInternals() {
        ArchitectureRules.assertMainJavaDoesNotImportForbiddenPackages(
                "focela-infra",
                List.of("com.focela.platform.system."),
                List.of("com.focela.platform.system.api."),
                List.of());
    }
}
