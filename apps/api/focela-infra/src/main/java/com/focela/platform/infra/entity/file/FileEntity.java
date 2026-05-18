package com.focela.platform.infra.entity.file;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * File table
 * Each file upload records an entry in this table
 */
@TableName("infra_file")
@KeySequence("infra_file_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class FileEntity extends BaseEntity {

    /**
     * ID, database auto-increment
     */
    private Long id;
    /**
     * Config ID
     *
     * Associated with {@link FileConfigEntity#getId()}
     */
    private Long configId;
    /**
     * Original file name
     */
    private String name;
    /**
     * Path, i.e. file name
     */
    private String path;
    /**
     * Access URL
     */
    private String url;
    /**
     * File MIME type, e.g. "application/octet-stream"
     */
    private String type;
    /**
     * File size
     */
    private Long size;

}
