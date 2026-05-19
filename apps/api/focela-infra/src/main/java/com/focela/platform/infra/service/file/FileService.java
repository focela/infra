package com.focela.platform.infra.service.file;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.file.dto.FileCreateRequest;
import com.focela.platform.infra.controller.admin.file.dto.FilePageRequest;
import com.focela.platform.infra.controller.admin.file.dto.FilePresignedUrlResponse;
import com.focela.platform.infra.domain.entity.file.FileEntity;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * File Service interface
 */
public interface FileService {

    /**
     * Get a paged list of files.
     *
     * @param pageRequest paged query
     * @return paged files
     */
    PageResult<FileEntity> getFilePage(FilePageRequest pageRequest);

    /**
     * Save a file and return the access path.
     *
     * @param content   file content
     * @param name      file name, may be empty
     * @param directory directory, may be empty
     * @param type      MIME type of the file, may be empty
     * @return file path
     */
    String createFile(@NotEmpty(message = "file content must not be blank") byte[] content,
                      String name, String directory, String type);

    /**
     * Generate a presigned URL for uploading the file.
     *
     * @param name      file name
     * @param directory directory
     * @return presigned URL info
     */
    FilePresignedUrlResponse presignPutUrl(@NotEmpty(message = "file name must not be blank") String name,
                                         String directory);
    /**
     * Generate a presigned URL for reading the file.
     *
     * @param url full file access URL
     * @param expirationSeconds expiration in seconds
     * @return presigned URL
     */
    String presignGetUrl(String url, Integer expirationSeconds);

    /**
     * Create a file record.
     *
     * @param createRequest creation info
     * @return ID
     */
    Long createFile(FileCreateRequest createRequest);
    FileEntity getFile(Long id);

    /**
     * Delete a file.
     *
     * @param id ID
     */
    void deleteFile(Long id) throws Exception;

    /**
     * Batch delete files.
     *
     * @param ids ID list
     */
    void deleteFileList(List<Long> ids) throws Exception;

    /**
     * Get file content.
     *
     * @param configId config ID
     * @param path     file path
     * @return file content
     */
    byte[] getFileContent(Long configId, String path) throws Exception;

}
