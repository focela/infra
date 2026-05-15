package com.focela.platform.common.utils.io;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.SneakyThrows;

import java.io.File;

/**
 * File utility class
 */
public class FileUtils {

    /**
     * Create a temporary file.
     * The file is deleted when the JVM exits.
     *
     * @param data file content
     * @return the file
     */
    @SneakyThrows
    public static File createTempFile(String data) {
        File file = createTempFile();
        // write content
        FileUtil.writeUtf8String(data, file);
        return file;
    }

    /**
     * Create a temporary file.
     * The file is deleted when the JVM exits.
     *
     * @param data file content
     * @return the file
     */
    @SneakyThrows
    public static File createTempFile(byte[] data) {
        File file = createTempFile();
        // write content
        FileUtil.writeBytes(data, file);
        return file;
    }

    /**
     * Create an empty temporary file.
     * The file is deleted when the JVM exits.
     *
     * @return the file
     */
    @SneakyThrows
    public static File createTempFile() {
        // create file with UUID to ensure uniqueness
        File file = File.createTempFile(IdUtil.simpleUUID(), null);
        // mark for automatic deletion on JVM exit
        file.deleteOnExit();
        return file;
    }

}
