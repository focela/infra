package com.focela.platform.protection.arch;

import com.focela.platform.test.core.arch.ArchitectureRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Enforces architecture conventions for the service-protection starter.
 */
class ProtectionArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                        "com.focela.platform.idempotent",
                        "com.focela.platform.lock4j",
                        "com.focela.platform.ratelimiter",
                        "com.focela.platform.signature");
    }

    @Test
    void configurationLocation() {
        ArchitectureRules.CONFIGURATION_RESIDES_IN_CONFIG_PACKAGE.check(classes);
    }

    @Test
    void autoConfigurationLocation() {
        ArchitectureRules.AUTO_CONFIGURATION_RESIDES_IN_CONFIG_PACKAGE.check(classes);
    }

    @Test
    void mainJavaContainsOnlyJavaFiles() {
        ArchitectureRules.assertMainJavaContainsOnlyJavaFiles("focela-spring-boot-starter-protection");
    }

    @Test
    void legacyTestPrefixShouldNotBeUsed() {
        ArchitectureRules.assertTestMethodNamesDoNotUseLegacyPrefix("focela-spring-boot-starter-protection");
    }
}
