package com.focela.platform;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.collection.SetUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.io.File.separator;

/**
 * Project reactor: one-click replacement of Maven groupId, artifactId, project package, etc.
 * <p>
 * Configure via the groupIdNew, artifactIdNew, and projectBaseDirNew variables.
 */
@Slf4j
public class ProjectReactor {

    private static final String GROUP_ID = "com.focela.platform";
    private static final String ARTIFACT_ID = "focela-platform";
    private static final String PACKAGE_NAME = "com.focela.platform";
    private static final String TITLE = "Focela Admin System";

    /**
     * Whitelisted file types that are copied as-is without rewriting.
     */
    private static final Set<String> WHITE_FILE_TYPES = SetUtils.asSet("gif", "jpg", "svg", "png", // images
            "eot", "woff2", "ttf", "woff",  // fonts
            "xdb"); // IP database

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String projectBaseDir = getProjectBaseDir();
        log.info("[main][original project path ({})]", projectBaseDir);

        // ========== Configuration; edit manually ==========
        String groupIdNew = "cn.star.gg";
        String artifactIdNew = "star";
        String packageNameNew = "cn.start.pp";
        String titleNew = "Potato Admin System";
        String projectBaseDirNew = projectBaseDir + "-new"; // Directory for the renamed project
        log.info("[main][checking whether new project directory ({}) exists]", projectBaseDirNew);
        if (FileUtil.exist(projectBaseDirNew)) {
            log.error("[main][new project directory ({}) already exists; please choose a different directory. Exiting.]", projectBaseDirNew);
            return;
        }
        // If the new directory contains keywords like PACKAGE_NAME or ARTIFACT_ID, the path will be substituted
        // and generated files will end up outside the expected directory.
        if (StrUtil.containsAny(projectBaseDirNew, PACKAGE_NAME, ARTIFACT_ID, StrUtil.upperFirst(ARTIFACT_ID))) {
            log.error("[main][new project directory `projectBaseDirNew` ({}) conflicts with name '{}' or '{}'; please choose a different directory. Exiting.]",
                    projectBaseDirNew, PACKAGE_NAME, ARTIFACT_ID);
            return;
        }
        log.info("[main][new project directory check complete; new project path ({})]", projectBaseDirNew);
        // Collect files to copy
        log.info("[main][collecting files to rewrite; this takes about 10-20 seconds]");
        Collection<File> files = listFiles(projectBaseDir);
        log.info("[main][file count to rewrite: {}; takes about 15-30 seconds]", files.size());
        // Write files
        files.forEach(file -> {
            // For whitelisted file types, copy directly without rewriting
            String fileType = getFileType(file);
            if (WHITE_FILE_TYPES.contains(fileType)) {
                copyFile(file, projectBaseDir, projectBaseDirNew, packageNameNew, artifactIdNew);
                return;
            }
            // For non-whitelisted file types, rewrite the content and generate the file
            String content = replaceFileContent(file, groupIdNew, artifactIdNew, packageNameNew, titleNew);
            writeFile(file, content, projectBaseDir, projectBaseDirNew, packageNameNew, artifactIdNew);
        });
        log.info("[main][rewrite complete] total elapsed: {} seconds", (System.currentTimeMillis() - start) / 1000);
    }

    private static String getProjectBaseDir() {
        String baseDir = System.getProperty("user.dir");
        if (StrUtil.isEmpty(baseDir)) {
            throw new NullPointerException("project base path does not exist");
        }
        return baseDir;
    }

    private static Collection<File> listFiles(String projectBaseDir) {
        Collection<File> files = FileUtil.loopFiles(projectBaseDir);
        // Remove IDEA, Git's own files, and Node build outputs
        files = files.stream()
                .filter(file -> !file.getPath().contains(separator + "target" + separator)
                        && !file.getPath().contains(separator + "node_modules" + separator)
                        && !file.getPath().contains(separator + ".idea" + separator)
                        && !file.getPath().contains(separator + ".git" + separator)
                        && !file.getPath().contains(separator + "dist" + separator)
                        && !file.getPath().contains(".iml")
                        && !file.getPath().contains(".html.gz"))
                .collect(Collectors.toList());
        return files;
    }

    private static String replaceFileContent(File file, String groupIdNew,
                                             String artifactIdNew, String packageNameNew,
                                             String titleNew) {
        String content = FileUtil.readString(file, StandardCharsets.UTF_8);
        // Skip rewriting for whitelisted file types
        String fileType = getFileType(file);
        if (WHITE_FILE_TYPES.contains(fileType)) {
            return content;
        }
        // Rewrite the file content
        return content.replaceAll(GROUP_ID, groupIdNew)
                .replaceAll(PACKAGE_NAME, packageNameNew)
                .replaceAll(ARTIFACT_ID, artifactIdNew) // Must be replaced last because ARTIFACT_ID is too short!
                .replaceAll(StrUtil.upperFirst(ARTIFACT_ID), StrUtil.upperFirst(artifactIdNew))
                .replaceAll(TITLE, titleNew);
    }

    private static void writeFile(File file, String fileContent, String projectBaseDir,
                                  String projectBaseDirNew, String packageNameNew, String artifactIdNew) {
        String newPath = buildNewFilePath(file, projectBaseDir, projectBaseDirNew, packageNameNew, artifactIdNew);
        FileUtil.writeUtf8String(fileContent, newPath);
    }

    private static void copyFile(File file, String projectBaseDir,
                                 String projectBaseDirNew, String packageNameNew, String artifactIdNew) {
        String newPath = buildNewFilePath(file, projectBaseDir, projectBaseDirNew, packageNameNew, artifactIdNew);
        FileUtil.copyFile(file, new File(newPath));
    }

    private static String buildNewFilePath(File file, String projectBaseDir,
                                           String projectBaseDirNew, String packageNameNew, String artifactIdNew) {
        return file.getPath().replace(projectBaseDir, projectBaseDirNew) // New directory
                .replace(PACKAGE_NAME.replaceAll("\\.", Matcher.quoteReplacement(separator)),
                        packageNameNew.replaceAll("\\.", Matcher.quoteReplacement(separator)))
                .replace(ARTIFACT_ID, artifactIdNew) //
                .replaceAll(StrUtil.upperFirst(ARTIFACT_ID), StrUtil.upperFirst(artifactIdNew));
    }

    private static String getFileType(File file) {
        return file.length() > 0 ? FileTypeUtil.getType(file) : "";
    }

}
