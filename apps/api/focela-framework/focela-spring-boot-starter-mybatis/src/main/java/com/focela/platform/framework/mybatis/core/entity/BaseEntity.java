package com.focela.platform.framework.mybatis.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fhs.core.trans.vo.TransPojo;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity object.
 *
 * Why implement the {@link TransPojo} interface?
 * Because Easy-Trans TransType.SIMPLE mode integrates with MyBatis Plus queries.
 */
@Data
@JsonIgnoreProperties(value = "transMap") // Easy-Trans adds a transMap field; ignore it to avoid Jackson deserialization errors in Spring Cache
public abstract class BaseEntity implements Serializable, TransPojo {

    /**
     * Creation time
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * Last update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * Creator; currently the SysUser ID.
     *
     * Stored as String because non-numeric values may appear in the future; reserves room for extension.
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String creator;
    /**
     * Updater; currently the SysUser ID.
     *
     * Stored as String because non-numeric values may appear in the future; reserves room for extension.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updater;
    /**
     * Whether deleted
     */
    @TableLogic
    private Boolean deleted;

    /**
     * Clear creator, createTime, updateTime, and updater to prevent the frontend from passing these fields and overwriting them directly.
     */
    public void clean(){
        this.creator = null;
        this.createTime = null;
        this.updater = null;
        this.updateTime = null;
    }

}
