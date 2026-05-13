package com.focela.platform.module.system.config.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.module.system.entity.department.DepartmentEntity;
import com.focela.platform.module.system.service.department.DepartmentService;
import com.mzt.logapi.service.IParseFunction;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 部门名字的 {@link IParseFunction} 实现类
 */
@Slf4j
@Component
public class DepartmentParseFunction implements IParseFunction {

    public static final String NAME = "getDeptById";

    @Resource
    private DepartmentService deptService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // 获取部门信息
        DepartmentEntity dept = deptService.getDept(Convert.toLong(value));
        if (dept == null) {
            log.warn("[apply][get department {{}}is empty", value);
            return "";
        }
        return dept.getName();
    }

}
