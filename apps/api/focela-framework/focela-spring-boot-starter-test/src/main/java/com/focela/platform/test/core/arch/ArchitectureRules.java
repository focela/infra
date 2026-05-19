package com.focela.platform.test.core.arch;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Shared architecture rules enforced by every business module.
 *
 * <p>Rules are exposed as static {@link ArchRule} constants and as a
 * {@link #importModule(String)} helper that returns a scoped
 * {@link JavaClasses} (production classes only). Concrete tests in each
 * module use the JUnit Jupiter {@code @Test} style and call
 * {@code rule.check(importedClasses)} directly — this avoids the
 * Surefire/ArchUnit JUnit Platform engine discovery issue.
 *
 * <p>See {@code docs/MODULE_TEMPLATE.md} for the rationale behind each rule.
 */
public final class ArchitectureRules {

    private ArchitectureRules() {
    }

    /** Imports the given module's production classes (excluding tests). */
    public static JavaClasses importModule(String basePackage) {
        return new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(basePackage);
    }

    // ---- Naming + location ----

    public static final ArchRule CONTROLLER_RESIDES_IN_CONTROLLER_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAnyPackage(
                            "..controller.admin..",
                            "..controller.app..")
                    .as("REST controllers must live under controller/admin or controller/app");

    public static final ArchRule ENTITY_RESIDES_IN_ENTITY_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Entity")
                    .should().resideInAPackage("..domain.entity..")
                    .as("MyBatis entities (*Entity) must live under the domain/entity/ package");

    public static final ArchRule MAPPER_RESIDES_IN_REPOSITORY_MAPPER_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Mapper")
                    .should().resideInAPackage("..repository.mapper..")
                    .as("MyBatis mappers (*Mapper) must live under the repository/mapper/ package");

    // Spring @Configuration classes (excluding *AutoConfiguration — those are framework-starter
    // SPI and live under <starter>/config) must live in the module's config/ package.
    public static final ArchRule CONFIGURATION_RESIDES_IN_CONFIG_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Configuration")
                    .and().haveSimpleNameNotEndingWith("AutoConfiguration")
                    .should().resideInAPackage("..config..")
                    .as("*Configuration classes must live under the config/ package "
                            + "(Focela*AutoConfiguration is the starter-SPI variant and is exempt)");

    public static final ArchRule CONSTANTS_RESIDES_IN_CONSTANTS_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Constants")
                    .should().resideInAPackage("..constants..")
                    .as("Constants holders (*Constants) must live under the constants/ package");

    public static final ArchRule ENUM_RESIDES_IN_ENUMS_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Enum")
                    .should().resideInAPackage("..enums..")
                    .as("Enum classes (*Enum) must live under an enums/ package");

    // ---- Spring DI style ----
    // Field injection (@Resource / @Autowired on a field) is discouraged by the Spring team's
    // official guidance. New code should use @RequiredArgsConstructor + private final fields.
    // This convention is enforced by manual code review (and the §12.5 doc in MODULE_TEMPLATE.md)
    // rather than ArchUnit, because the two real exceptions in the codebase — @Autowired(required
    // = false) for optional beans, and @Resource @Lazy for circular-dep cycle breaks — cannot be
    // expressed cleanly as a single ArchUnit rule without false-positive-prone freeze logic.

    // ---- Layered dependency direction ----

    public static final ArchRule CONTROLLER_DOES_NOT_USE_REPOSITORY =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .as("Controllers must not call the repository layer directly; go through service/");

    // Services and repositories may consume controller-tier DTOs (a deliberate MyBatis-Plus
    // pattern: *PageRequest is passed straight into the Mapper for the WHERE clause). We
    // forbid only dependencies on controller *production* classes (controllers themselves).
    private static final com.tngtech.archunit.base.DescribedPredicate<JavaClass> IN_CONTROLLER_NOT_DTO =
            resideInAPackage("..controller..").and(not(resideInAPackage("..controller..dto..")));

    public static final ArchRule SERVICE_DOES_NOT_USE_CONTROLLER =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat(IN_CONTROLLER_NOT_DTO)
                    .as("Services must not depend on controller production classes (DTOs excluded)");

    public static final ArchRule REPOSITORY_DOES_NOT_USE_CONTROLLER =
            noClasses().that().resideInAPackage("..repository..")
                    .should().dependOnClassesThat(IN_CONTROLLER_NOT_DTO)
                    .as("Repositories must not depend on controller production classes (DTOs excluded)");

    public static final ArchRule REPOSITORY_DOES_NOT_USE_SERVICE =
            noClasses().that().resideInAPackage("..repository..")
                    .should().dependOnClassesThat().resideInAPackage("..service..")
                    .as("Repositories must not depend on services");

    // ---- Controller-tier DTO suffixes ----

    public static final ArchRule CONTROLLER_DTO_HAS_APPROVED_SUFFIX =
            classes().that().resideInAPackage("..controller..dto..")
                    .and().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("Request")
                    .orShould().haveSimpleNameEndingWith("Response")
                    .orShould().haveSimpleNameEndingWith("ExcelRow")
                    .as("Controller DTOs must end with Request / Response / ExcelRow"
                            + " (covers *Request, *SaveRequest, *PageRequest, *Response)");

    // ---- Cross-module DTO suffixes (focela-common api/ DTOs) ----

    public static final ArchRule CROSS_MODULE_DTO_HAS_RPC_SUFFIX =
            classes().that().resideInAPackage("..common.api..dto..")
                    .and().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("RpcRequest")
                    .orShould().haveSimpleNameEndingWith("RpcResponse")
                    .as("Cross-module DTOs in focela-common api/.../dto must end with"
                            + " RpcRequest or RpcResponse");

    // ---- Cross-module isolation ----

    /**
     * Builds a rule asserting that classes in {@code thisModuleBasePkg} reach
     * into {@code otherModuleBasePkg} only through the {@code .api.} contract
     * package. All other sub-packages of the other module (service, repository,
     * entity, controller, …) are off-limits.
     */
    public static ArchRule moduleDoesNotReachOtherModuleInternals(
            String thisModuleBasePkg, String otherModuleBasePkg) {
        com.tngtech.archunit.base.DescribedPredicate<JavaClass> otherInternals =
                resideInAPackage(otherModuleBasePkg + "..")
                        .and(not(resideInAPackage(otherModuleBasePkg + ".api..")));
        return noClasses().that().resideInAPackage(thisModuleBasePkg + "..")
                .should().dependOnClassesThat(otherInternals)
                .as(thisModuleBasePkg + " must reach " + otherModuleBasePkg
                        + " only through the .api. contract package");
    }
}
