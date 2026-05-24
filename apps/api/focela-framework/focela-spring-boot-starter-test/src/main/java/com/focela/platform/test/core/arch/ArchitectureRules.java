package com.focela.platform.test.core.arch;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    private static final Pattern LEGACY_TEST_METHOD_PATTERN =
            Pattern.compile("\\b(?:public\\s+)?void\\s+test[A-Z_][A-Za-z0-9_]*\\s*\\(");
    private static final Pattern LEGACY_ABBREVIATION_PATTERN =
            Pattern.compile("\\b(?:dictType|deptId|deptIds|dataScopeDeptIds"
                    + "|getDict[A-Za-z0-9_]*|setDict[A-Za-z0-9_]*"
                    + "|getDept[A-Za-z0-9_]*|setDept[A-Za-z0-9_]*)\\b");

    private ArchitectureRules() {
    }

    /** Imports the given module's production classes (excluding tests). */
    public static JavaClasses importModule(String basePackage) {
        return new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(basePackage);
    }

    /**
     * Verifies that a module's {@code src/main/java} tree contains Java source files only.
     * HTTP samples, SQL snippets, and other development artifacts must live under docs/ or
     * test resources so they are not mixed into the production source tree.
     */
    public static void assertMainJavaContainsOnlyJavaFiles(String moduleDirectoryName) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path sourceRoot = moduleRoot.resolve("src/main/java");
        if (!Files.exists(sourceRoot)) {
            throw new AssertionError("Cannot find source root: " + sourceRoot.toAbsolutePath().normalize());
        }
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<String> nonJavaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.getFileName().toString().endsWith(".java"))
                    .map(sourceRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .toList();
            if (!nonJavaFiles.isEmpty()) {
                throw new AssertionError("src/main/java must contain only .java files: " + nonJavaFiles);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan source root: " + sourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Verifies that JUnit test method names describe behavior directly instead of
     * carrying the legacy {@code testXxx} prefix.
     */
    public static void assertTestMethodNamesDoNotUseLegacyPrefix(String moduleDirectoryName) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path testSourceRoot = moduleRoot.resolve("src/test/java");
        if (!Files.exists(testSourceRoot)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(testSourceRoot)) {
            List<Path> testFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
            List<String> violations = new ArrayList<>();
            for (Path testFile : testFiles) {
                List<String> lines = Files.readAllLines(testFile);
                for (int i = 0; i < lines.size(); i++) {
                    if (LEGACY_TEST_METHOD_PATTERN.matcher(lines.get(i)).find()) {
                        violations.add(testSourceRoot.relativize(testFile) + ":" + (i + 1));
                    }
                }
            }
            if (!violations.isEmpty()) {
                throw new AssertionError("Test method names must not use the legacy testXxx prefix: " + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan test source root: "
                    + testSourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Prevents new source files from adopting legacy local abbreviations such as
     * {@code dictType} and {@code deptId}. Existing files stay allowlisted until
     * their public contracts and persistence mappings can be migrated safely.
     */
    public static void assertMainJavaLegacyAbbreviationsStayInAllowedFiles(
            String moduleDirectoryName, List<String> allowedRelativePaths) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path sourceRoot = moduleRoot.resolve("src/main/java");
        if (!Files.exists(sourceRoot)) {
            throw new AssertionError("Cannot find source root: " + sourceRoot.toAbsolutePath().normalize());
        }
        List<String> normalizedAllowedPaths = allowedRelativePaths.stream()
                .map(ArchitectureRules::normalizeRelativePath)
                .toList();
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<Path> sourceFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
            List<String> violations = new ArrayList<>();
            for (Path sourceFile : sourceFiles) {
                String relativePath = normalizeRelativePath(moduleRoot.relativize(sourceFile).toString());
                if (normalizedAllowedPaths.contains(relativePath)) {
                    continue;
                }
                List<String> lines = Files.readAllLines(sourceFile);
                for (int i = 0; i < lines.size(); i++) {
                    if (LEGACY_ABBREVIATION_PATTERN.matcher(lines.get(i)).find()) {
                        violations.add(relativePath + ":" + (i + 1));
                    }
                }
            }
            if (!violations.isEmpty()) {
                throw new AssertionError("Use full English names instead of legacy abbreviations: " + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan source root: "
                    + sourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Verifies that source files do not import implementation packages from another module.
     * Contract packages can be allowlisted, for example {@code com.focela.platform.infra.api.}.
     */
    public static void assertMainJavaDoesNotImportForbiddenPackages(
            String moduleDirectoryName,
            List<String> forbiddenImportPrefixes,
            List<String> allowedImportPrefixes,
            List<String> allowedRelativePaths) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path sourceRoot = moduleRoot.resolve("src/main/java");
        if (!Files.exists(sourceRoot)) {
            throw new AssertionError("Cannot find source root: " + sourceRoot.toAbsolutePath().normalize());
        }
        List<String> normalizedAllowedPaths = allowedRelativePaths.stream()
                .map(ArchitectureRules::normalizeRelativePath)
                .toList();
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<Path> sourceFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
            List<String> violations = new ArrayList<>();
            for (Path sourceFile : sourceFiles) {
                String relativePath = normalizeRelativePath(moduleRoot.relativize(sourceFile).toString());
                if (normalizedAllowedPaths.contains(relativePath)) {
                    continue;
                }
                List<String> lines = Files.readAllLines(sourceFile);
                for (int i = 0; i < lines.size(); i++) {
                    String importedClass = extractImportedClass(lines.get(i));
                    if (importedClass == null || isAllowedImport(importedClass, allowedImportPrefixes)) {
                        continue;
                    }
                    if (startsWithAny(importedClass, forbiddenImportPrefixes)) {
                        violations.add(relativePath + ":" + (i + 1) + " -> " + importedClass);
                    }
                }
            }
            if (!violations.isEmpty()) {
                throw new AssertionError("Module source imports forbidden implementation packages: " + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan source root: "
                    + sourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Prevents new non-controller classes from depending on REST request/response payloads.
     * Existing legacy files can stay allowlisted while the module migrates toward service-level
     * command/query objects.
     */
    public static void assertControllerPayloadImportsStayInAllowedFiles(
            String moduleDirectoryName, List<String> allowedRelativePaths) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path sourceRoot = moduleRoot.resolve("src/main/java");
        if (!Files.exists(sourceRoot)) {
            throw new AssertionError("Cannot find source root: " + sourceRoot.toAbsolutePath().normalize());
        }
        List<String> normalizedAllowedPaths = allowedRelativePaths.stream()
                .map(ArchitectureRules::normalizeRelativePath)
                .toList();
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<Path> sourceFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
            List<String> violations = new ArrayList<>();
            for (Path sourceFile : sourceFiles) {
                String relativePath = normalizeRelativePath(moduleRoot.relativize(sourceFile).toString());
                if (relativePath.contains("/controller/") || normalizedAllowedPaths.contains(relativePath)) {
                    continue;
                }
                List<String> lines = Files.readAllLines(sourceFile);
                for (int i = 0; i < lines.size(); i++) {
                    String importedClass = extractImportedClass(lines.get(i));
                    if (importedClass != null && isControllerPayloadImport(importedClass)) {
                        violations.add(relativePath + ":" + (i + 1) + " -> " + importedClass);
                    }
                }
            }
            if (!violations.isEmpty()) {
                throw new AssertionError("Non-controller classes must not add new controller payload imports: "
                        + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan source root: "
                    + sourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Prevents REST controllers for one surface (admin or app) from reusing payloads or
     * controllers from the other surface. Existing bridge files can stay allowlisted until
     * their API contracts are split safely.
     */
    public static void assertAdminAppControllerImportsStayInAllowedFiles(
            String moduleDirectoryName,
            String moduleBasePackage,
            List<String> allowedRelativePaths) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path sourceRoot = moduleRoot.resolve("src/main/java");
        if (!Files.exists(sourceRoot)) {
            throw new AssertionError("Cannot find source root: " + sourceRoot.toAbsolutePath().normalize());
        }
        List<String> normalizedAllowedPaths = allowedRelativePaths.stream()
                .map(ArchitectureRules::normalizeRelativePath)
                .toList();
        String adminControllerPrefix = moduleBasePackage + ".controller.admin.";
        String appControllerPrefix = moduleBasePackage + ".controller.app.";
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<Path> sourceFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
            List<String> violations = new ArrayList<>();
            for (Path sourceFile : sourceFiles) {
                String relativePath = normalizeRelativePath(moduleRoot.relativize(sourceFile).toString());
                boolean adminController = relativePath.contains("/controller/admin/");
                boolean appController = relativePath.contains("/controller/app/");
                if ((!adminController && !appController) || normalizedAllowedPaths.contains(relativePath)) {
                    continue;
                }
                List<String> lines = Files.readAllLines(sourceFile);
                for (int i = 0; i < lines.size(); i++) {
                    String importedClass = extractImportedClass(lines.get(i));
                    if (importedClass == null) {
                        continue;
                    }
                    if ((adminController && importedClass.startsWith(appControllerPrefix))
                            || (appController && importedClass.startsWith(adminControllerPrefix))) {
                        violations.add(relativePath + ":" + (i + 1) + " -> " + importedClass);
                    }
                }
            }
            if (!violations.isEmpty()) {
                throw new AssertionError("Admin and app controllers must not import each other's API surface: "
                        + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to scan source root: "
                    + sourceRoot.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Spring Boot auto-configuration imports should point to classes named
     * {@code *AutoConfiguration}. Existing legacy names can be allowlisted until
     * they are renamed together with their metadata entries.
     */
    public static void assertAutoConfigurationImportsUseAutoConfigurationSuffix(
            String moduleDirectoryName, List<String> allowedClassNames) {
        Path moduleRoot = resolveModuleRoot(moduleDirectoryName);
        Path importsFile = moduleRoot.resolve(
                "src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        if (!Files.exists(importsFile)) {
            return;
        }
        try {
            List<String> violations = Files.readAllLines(importsFile).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> !line.endsWith("AutoConfiguration"))
                    .filter(line -> !allowedClassNames.contains(line))
                    .sorted()
                    .toList();
            if (!violations.isEmpty()) {
                throw new AssertionError("Auto-configuration imports must use *AutoConfiguration names: "
                        + violations);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to read auto-configuration imports: "
                    + importsFile.toAbsolutePath().normalize(), e);
        }
    }

    /**
     * Prevents modules from silently adding direct dependencies on other Focela artifacts.
     * This keeps Maven-level dependency direction explicit while larger module splits are deferred.
     */
    public static void assertProjectDependenciesStayWithinAllowedArtifacts(
            String moduleDirectoryName, List<String> allowedArtifactIds) {
        Path pomFile = resolveModuleRoot(moduleDirectoryName).resolve("pom.xml");
        if (!Files.exists(pomFile)) {
            throw new AssertionError("Cannot find pom.xml: " + pomFile.toAbsolutePath().normalize());
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(pomFile.toFile());
            NodeList dependencies = document.getElementsByTagNameNS("*", "dependency");
            List<String> violations = new ArrayList<>();
            for (int i = 0; i < dependencies.getLength(); i++) {
                Element dependency = (Element) dependencies.item(i);
                String groupId = childText(dependency, "groupId");
                String artifactId = childText(dependency, "artifactId");
                if ("com.focela.platform".equals(groupId) && !allowedArtifactIds.contains(artifactId)) {
                    violations.add(artifactId);
                }
            }
            if (!violations.isEmpty()) {
                throw new AssertionError("Direct Focela project dependencies must be explicitly approved: "
                        + violations);
            }
        } catch (Exception e) {
            throw new AssertionError("Failed to inspect pom.xml dependencies: "
                    + pomFile.toAbsolutePath().normalize(), e);
        }
    }

    private static String normalizeRelativePath(String path) {
        return Path.of(path).normalize().toString().replace('\\', '/');
    }

    private static String childText(Element element, String localName) {
        NodeList children = element.getElementsByTagNameNS("*", localName);
        if (children.getLength() == 0) {
            return "";
        }
        return children.item(0).getTextContent().trim();
    }

    private static String extractImportedClass(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("import static ")) {
            return trimmed.substring("import static ".length(), trimmed.length() - 1);
        }
        if (trimmed.startsWith("import ")) {
            return trimmed.substring("import ".length(), trimmed.length() - 1);
        }
        return null;
    }

    private static boolean isAllowedImport(String importedClass, List<String> allowedImportPrefixes) {
        return startsWithAny(importedClass, allowedImportPrefixes);
    }

    private static boolean isControllerPayloadImport(String importedClass) {
        return importedClass.contains(".controller.")
                && (importedClass.contains(".request.") || importedClass.contains(".response."));
    }

    private static boolean startsWithAny(String value, List<String> prefixes) {
        return prefixes.stream().anyMatch(value::startsWith);
    }

    private static Path resolveModuleRoot(String moduleDirectoryName) {
        Path current = Path.of("").toAbsolutePath().normalize();
        if (current.getFileName() != null && current.getFileName().toString().equals(moduleDirectoryName)) {
            return current;
        }
        Path direct = Path.of(moduleDirectoryName);
        if (Files.exists(direct)) {
            return direct;
        }
        Path fromRepositoryRoot = Path.of("apps/api").resolve(moduleDirectoryName);
        if (Files.exists(fromRepositoryRoot)) {
            return fromRepositoryRoot;
        }
        return direct;
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

    // *RedisRepository covers two layouts: ..repository.redis.. inside business modules and
    // ..core.redis.. inside framework starters (which have no repository layer of their own).
    // Allow-empty so starters without a Redis repo don't fail the rule.
    public static final ArchRule REDIS_REPOSITORY_RESIDES_IN_REDIS_PACKAGE =
            classes().that().haveSimpleNameEndingWith("RedisRepository")
                    .should().resideInAPackage("..redis..")
                    .as("*RedisRepository classes must live under a redis/ sub-package")
                    .allowEmptyShould(true);

    // Spring @Configuration classes (excluding *AutoConfiguration — those are framework-starter
    // SPI and live under <starter>/config) must live in the module's config/ package.
    public static final ArchRule CONFIGURATION_RESIDES_IN_CONFIG_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Configuration")
                    .and().haveSimpleNameNotEndingWith("AutoConfiguration")
                    .should().resideInAPackage("..config..")
                    .as("*Configuration classes must live under the config/ package "
                            + "(Focela*AutoConfiguration is the starter-SPI variant and is exempt)")
                    .allowEmptyShould(true);

    public static final ArchRule AUTO_CONFIGURATION_RESIDES_IN_CONFIG_PACKAGE =
            classes().that().haveSimpleNameEndingWith("AutoConfiguration")
                    .should().resideInAPackage("..config..")
                    .as("*AutoConfiguration classes must live under the config/ package");

    public static final ArchRule CONSTANTS_RESIDES_IN_CONSTANTS_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Constants")
                    .should().resideInAPackage("..constants..")
                    .as("Constants holders (*Constants) must live under the constants/ package");

    public static final ArchRule ENUM_RESIDES_IN_ENUMS_PACKAGE =
            classes().that().haveSimpleNameEndingWith("Enum")
                    .should().resideInAPackage("..enums..")
                    .as("Enum classes (*Enum) must live under an enums/ package");

    // ---- Forbidden legacy naming suffixes ----
    // The codebase deliberately uses *Entity (not *DO/*PO), *Request/*Response (not *VO/*DTO/
    // *ReqVO/*RespVO), *Mapper (not *Dao/*DAO), Default*Service (not *ServiceImpl), and *Utils
    // (not *Util/*Helper). See MODULE_TEMPLATE.md §Naming.
    public static final ArchRule NO_LEGACY_NAMING_SUFFIXES =
            noClasses().that().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("DO")
                    .orShould().haveSimpleNameEndingWith("DTO")
                    .orShould().haveSimpleNameEndingWith("VO")
                    .orShould().haveSimpleNameEndingWith("Dao")
                    .orShould().haveSimpleNameEndingWith("DAO")
                    .orShould().haveSimpleNameEndingWith("ServiceImpl")
                    .orShould().haveSimpleNameEndingWith("Util")
                    .orShould().haveSimpleNameEndingWith("Helper")
                    .as("Legacy naming suffixes are forbidden: use *Entity (not *DO), "
                            + "*Request/*Response (not *VO/*DTO/*ReqVO/*RespVO), "
                            + "*Mapper (not *Dao/*DAO), Default*Service (not *ServiceImpl), "
                            + "*Utils (not *Util/*Helper)")
                    .allowEmptyShould(true);

    // ---- Forbidden legacy package dependencies ----
    // The project was migrated off cn.iocoder/yudao. Re-introducing those packages is a regression.
    public static final ArchRule NO_LEGACY_PACKAGE_DEPENDENCIES =
            noClasses().should().dependOnClassesThat()
                    .resideInAnyPackage("cn.iocoder..", "com.iocoder..", "..yudao..")
                    .as("Legacy packages (cn.iocoder, com.iocoder, *yudao*) must not be referenced")
                    .allowEmptyShould(true);

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

    // Services and repositories may consume controller-tier request/response classes (a deliberate
    // MyBatis-Plus pattern: *PageRequest is passed straight into the Mapper for the WHERE clause).
    // We forbid only dependencies on controller *production* classes (controllers themselves).
    private static final com.tngtech.archunit.base.DescribedPredicate<JavaClass> IN_CONTROLLER_NOT_PAYLOAD =
            resideInAPackage("..controller..")
                    .and(not(resideInAPackage("..controller..request..")))
                    .and(not(resideInAPackage("..controller..response..")));

    public static final ArchRule SERVICE_DOES_NOT_USE_CONTROLLER =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat(IN_CONTROLLER_NOT_PAYLOAD)
                    .as("Services must not depend on controller production classes (request/response excluded)");

    public static final ArchRule REPOSITORY_DOES_NOT_USE_CONTROLLER =
            noClasses().that().resideInAPackage("..repository..")
                    .should().dependOnClassesThat(IN_CONTROLLER_NOT_PAYLOAD)
                    .as("Repositories must not depend on controller production classes (request/response excluded)");

    public static final ArchRule REPOSITORY_DOES_NOT_USE_SERVICE =
            noClasses().that().resideInAPackage("..repository..")
                    .should().dependOnClassesThat().resideInAPackage("..service..")
                    .as("Repositories must not depend on services");

    // ---- Controller-tier payload suffixes ----

    public static final ArchRule CONTROLLER_REQUEST_HAS_APPROVED_SUFFIX =
            classes().that().resideInAPackage("..controller..request..")
                    .and().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("Request")
                    .orShould().haveSimpleNameEndingWith("ExcelRow")
                    .as("Classes under controller/.../request/ must end with Request or ExcelRow");

    public static final ArchRule CONTROLLER_RESPONSE_HAS_APPROVED_SUFFIX =
            classes().that().resideInAPackage("..controller..response..")
                    .and().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("Response")
                    .as("Classes under controller/.../response/ must end with Response");

    public static final ArchRule CONTROLLER_REQUEST_RESIDES_IN_REQUEST_PACKAGE =
            classes().that().resideInAPackage("..controller..")
                    .and().haveSimpleNameEndingWith("Request")
                    .and().areTopLevelClasses()
                    .should().resideInAPackage("..controller..request..")
                    .as("Controller request payloads must live under controller/.../request/");

    public static final ArchRule CONTROLLER_RESPONSE_RESIDES_IN_RESPONSE_PACKAGE =
            classes().that().resideInAPackage("..controller..")
                    .and().haveSimpleNameEndingWith("Response")
                    .and().areTopLevelClasses()
                    .should().resideInAPackage("..controller..response..")
                    .as("Controller response payloads must live under controller/.../response/");

    public static final ArchRule CONTROLLER_DOES_NOT_USE_DTO_PACKAGE =
            noClasses().that().resideInAPackage("..controller..dto..")
                    .should().resideInAPackage("..controller..dto..")
                    .allowEmptyShould(true)
                    .as("REST controller payloads must use request/response packages, not dto/");

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
