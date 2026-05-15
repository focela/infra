package com.focela.platform.infra.api.file;

import jakarta.validation.constraints.NotEmpty;

/**
 * File API interface
 */
public interface FileApi {

    /**
     * Save file and return the access path.
     *
     * @param content file content
     * @return file path
     */
    default String createFile(byte[] content) {
        return createFile(content, null, null, null);
    }

    /**
     * Save file and return the access path.
     *
     * @param content file content
     * @param name file name, may be empty
     * @return file path
     */
    default String createFile(byte[] content, String name) {
        return createFile(content, name, null, null);
    }

    /**
     * Save file and return the access path.
     *
     * @param content file content
     * @param name file name, may be empty
     * @param directory directory, may be empty
     * @param type MIME type of the file, may be empty
     * @return file path
     */
    String createFile(@NotEmpty(message = "file content must not be blank") byte[] content,
                      String name, String directory, String type);

    /**
     * Generate a presigned URL for reading the file.
     *
     * @param url full file access URL
     * @param expirationSeconds expiration in seconds
     * @return presigned URL
     */
    String presignGetUrl(@NotEmpty(message = "URL must not be blank") String url,
                         Integer expirationSeconds);

}
