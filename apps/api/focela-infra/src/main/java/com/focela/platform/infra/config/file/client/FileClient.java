package com.focela.platform.infra.config.file.client;

/**
 * File client
 */
public interface FileClient {

    /**
     * Get the client ID
     *
     * @return client ID
     */
    Long getId();

    /**
     * Upload file
     *
     * @param content file stream
     * @param path    relative path
     * @return full path, i.e. HTTP access URL
     * @throws Exception throws Exception when uploading file fails
     */
    String upload(byte[] content, String path, String type) throws Exception;

    /**
     * Delete file
     *
     * @param path relative path
     * @throws Exception throws Exception when deleting file fails
     */
    void delete(String path) throws Exception;

    /**
     * Get the file content
     *
     * @param path relative path
     * @return file content
     */
    byte[] getContent(String path) throws Exception;

    // ========== File signing, currently only S3 supports ==========

    /**
     * Get a presigned URL for file upload
     *
     * @param path relative path
     * @return file presigned URL
     */
    default String presignPutUrl(String path) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    /**
     * Generate a presigned URL for reading the file
     *
     * @param url full file access URL
     * @param expirationSeconds expiration duration, unit: seconds
     * @return file presigned URL
     */
    default String presignGetUrl(String url, Integer expirationSeconds) {
        throw new UnsupportedOperationException("unsupported operation");
    }

}
