package com.focela.platform.infra.arch;

import com.focela.platform.test.core.arch.ArchitectureRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Enforces the focela-infra module's architectural conventions. See
 * {@code docs/MODULE_TEMPLATE.md} for the rationale.
 */
class InfraArchitectureTest {

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
    void infraDoesNotReachSystem() {
        ArchitectureRules.moduleDoesNotReachOtherModuleInternals(
                "com.focela.platform.infra",
                "com.focela.platform.system").check(classes);
    }
}
