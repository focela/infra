package com.focela.platform.infra.config.file.core.enums;

import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.infra.config.file.core.client.FileClient;
import com.focela.platform.infra.config.file.core.client.FileClientConfig;
import com.focela.platform.infra.config.file.core.client.db.DBFileClient;
import com.focela.platform.infra.config.file.core.client.db.DBFileClientConfig;
import com.focela.platform.infra.config.file.core.client.ftp.FtpFileClient;
import com.focela.platform.infra.config.file.core.client.ftp.FtpFileClientConfig;
import com.focela.platform.infra.config.file.core.client.local.LocalFileClient;
import com.focela.platform.infra.config.file.core.client.local.LocalFileClientConfig;
import com.focela.platform.infra.config.file.core.client.s3.S3FileClient;
import com.focela.platform.infra.config.file.core.client.s3.S3FileClientConfig;
import com.focela.platform.infra.config.file.core.client.sftp.SftpFileClient;
import com.focela.platform.infra.config.file.core.client.sftp.SftpFileClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * File storage enum
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB(1, DBFileClientConfig.class, DBFileClient.class),

    LOCAL(10, LocalFileClientConfig.class, LocalFileClient.class),
    FTP(11, FtpFileClientConfig.class, FtpFileClient.class),
    SFTP(12, SftpFileClientConfig.class, SftpFileClient.class),

    S3(20, S3FileClientConfig.class, S3FileClient.class),
    ;

    /**
     * Storage
     */
    private final Integer storage;

    /**
     * Config class
     */
    private final Class<? extends FileClientConfig> configClass;
    /**
     * Client class
     */
    private final Class<? extends FileClient> clientClass;

    public static FileStorageEnum getByStorage(Integer storage) {
        return ArrayUtil.firstMatch(o -> o.getStorage().equals(storage), values());
    }

}
