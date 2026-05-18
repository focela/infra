package com.focela.platform.mybatis.core.handler;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.security.core.utils.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Default field auto-fill handler.
 *
 * If common fields are not explicitly assigned, this handler fills/assigns them.
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {

    @Override
    @SuppressWarnings("PatternVariableCanBeUsed")
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();

            LocalDateTime current = LocalDateTime.now();
            // If create time is null, use the current time as the insert time
            if (Objects.isNull(baseEntity.getCreateTime())) {
                baseEntity.setCreateTime(current);
            }
            // If update time is null, use the current time as the update time
            if (Objects.isNull(baseEntity.getUpdateTime())) {
                baseEntity.setUpdateTime(current);
            }

            Long userId = SecurityFrameworkUtils.getLoginUserId();
            // If the current logged-in user is not null and creator is null, set the current user as creator
            if (Objects.nonNull(userId) && Objects.isNull(baseEntity.getCreator())) {
                baseEntity.setCreator(userId.toString());
            }
            // If the current logged-in user is not null and updater is null, set the current user as updater
            if (Objects.nonNull(userId) && Objects.isNull(baseEntity.getUpdater())) {
                baseEntity.setUpdater(userId.toString());
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // If update time is null, use the current time as the update time
        Object modifyTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }

        // If the current logged-in user is not null and updater is null, set the current user as updater
        Object modifier = getFieldValByName("updater", metaObject);
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName("updater", userId.toString(), metaObject);
        }
    }
}
