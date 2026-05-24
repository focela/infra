package com.focela.platform.common.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Enforces the cross-module DTO suffix convention inside focela-common.
 * The rule is duplicated here (rather than imported from
 * focela-spring-boot-starter-test) to avoid a circular dependency:
 * starter-test depends on focela-common, so focela-common can only refer
 * to its own test classes.
 */
class CommonArchitectureTest {

    private static final List<String> LEGACY_CONSTANT_LOCATIONS = List.of(
            "com/focela/platform/common/enums/RpcConstants.java",
            "com/focela/platform/common/exception/enums/GlobalErrorCodeConstants.java"
    );

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

    @Test
    void constantsUseApprovedPackage() {
        Path sourceRoot = resolveSourceRoot();
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<String> violations = new ArrayList<>();
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("Constants.java"))
                    .forEach(path -> {
                        String relativePath = sourceRoot.relativize(path).toString().replace('\\', '/');
                        if (!relativePath.contains("/constants/")
                                && !LEGACY_CONSTANT_LOCATIONS.contains(relativePath)) {
                            violations.add(relativePath);
                        }
                    });
            if (!violations.isEmpty()) {
                throw new AssertionError("Common constants must live under common/constants: " + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan common source root: "
                    + sourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    private static Path resolveSourceRoot() {
        List<Path> candidates = List.of(
                Path.of("src/main/java"),
                Path.of("focela-framework/focela-common/src/main/java"),
                Path.of("apps/api/focela-framework/focela-common/src/main/java")
        );
        return candidates.stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Cannot find focela-common source root"));
    }
}
