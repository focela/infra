package com.focela.platform.module.infra.entity.file;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.utils.json.JsonUtils;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.module.infra.config.file.core.client.FileClientConfig;
import com.focela.platform.module.infra.config.file.core.client.db.DBFileClientConfig;
import com.focela.platform.module.infra.config.file.core.client.ftp.FtpFileClientConfig;
import com.focela.platform.module.infra.config.file.core.client.local.LocalFileClientConfig;
import com.focela.platform.module.infra.config.file.core.client.s3.S3FileClientConfig;
import com.focela.platform.module.infra.config.file.core.client.sftp.SftpFileClientConfig;
import com.focela.platform.module.infra.config.file.core.enums.FileStorageEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;

import java.lang.reflect.Field;

/**
 * File config table
 */
@TableName(value = "infra_file_config", autoResultMap = true)
@KeySequence("infra_file_config_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class FileConfigEntity extends BaseEntity {

    /**
     * Config ID, database auto-increment
     */
    private Long id;
    /**
     * Config name
     */
    private String name;
    /**
     * Storage
     *
     * Enum {@link FileStorageEnum}
     */
    private Integer storage;
    /**
     * Remarks
     */
    private String remark;
    /**
     * Whether master config
     *
     * Since multiple file configs can be configured, the master config is used by default for file upload
     */
    private Boolean master;

    /**
     * Payment channel config
     */
    @TableField(typeHandler = FileClientConfigTypeHandler.class)
    private FileClientConfig config;

    public static class FileClientConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        public FileClientConfigTypeHandler(Class<?> type) {
            super(type);
        }

        public FileClientConfigTypeHandler(Class<?> type, Field field) {
            super(type, field);
        }

        @Override
        public Object parse(String json) {
            FileClientConfig config = JsonUtils.parseObjectQuietly(json, new TypeReference<>() {
            });
            if (config != null) {
                return config;
            }

            // Compatibility with old version package paths
            String className = JsonUtils.parseObject(json, "@class", String.class);
            className = StrUtil.subAfter(className, ".", true);
            switch (className) {
                case "DBFileClientConfig":
                    return JsonUtils.parseObject2(json, DBFileClientConfig.class);
                case "FtpFileClientConfig":
                    return JsonUtils.parseObject2(json, FtpFileClientConfig.class);
                case "LocalFileClientConfig":
                    return JsonUtils.parseObject2(json, LocalFileClientConfig.class);
                case "SftpFileClientConfig":
                    return JsonUtils.parseObject2(json, SftpFileClientConfig.class);
                case "S3FileClientConfig":
                    return JsonUtils.parseObject2(json, S3FileClientConfig.class);
                default:
                    throw new IllegalArgumentException("unknown FileClientConfig type:" + json);
            }
        }

        @Override
        public String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

}
