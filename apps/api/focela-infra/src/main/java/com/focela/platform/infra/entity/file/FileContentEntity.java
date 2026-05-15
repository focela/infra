package com.focela.platform.infra.entity.file;

import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.config.file.core.client.db.DBFileClient;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * File content table
 *
 * Dedicated to storing file contents for {@link DBFileClient}
 */
@TableName("infra_file_content")
@KeySequence("infra_file_content_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class FileContentEntity extends BaseEntity {

    /**
     * ID, database auto-increment
     */
    @TableId
    private Long id;
    /**
     * Config ID
     *
     * Associated with {@link FileConfigEntity#getId()}
     */
    private Long configId;
    /**
     * Path, i.e. file name
     */
    private String path;
    /**
     * File content
     */
    private byte[] content;

}
