package com.focela.platform.common.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Enforces the cross-module DTO suffix convention inside focela-common.
 * The rule is duplicated here (rather than imported from
 * focela-spring-boot-starter-test) to avoid a circular dependency:
 * starter-test depends on focela-common, so focela-common can only refer
 * to its own test classes.
 */
class CommonArchitectureTest {

    private static JavaClasses apiClasses;

    @BeforeAll
    static void importClasses() {
        apiClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.focela.platform.common.api");
    }

    @Test
    void crossModuleDtoSuffix() {
        classes().that().resideInAPackage("..api..dto..")
                .and().areTopLevelClasses()
                .should().haveSimpleNameEndingWith("RpcRequest")
                .orShould().haveSimpleNameEndingWith("RpcResponse")
                .as("Cross-module DTOs in focela-common api/.../dto must end with"
                        + " RpcRequest or RpcResponse")
                .check(apiClasses);
    }
}
