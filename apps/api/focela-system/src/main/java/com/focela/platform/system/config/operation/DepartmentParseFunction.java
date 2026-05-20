package com.focela.platform.system.config.operation;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.service.department.DepartmentService;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link IParseFunction} implementation for department name
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentParseFunction implements IParseFunction {

    public static final String NAME = "getDeptById";

    private final DepartmentService departmentService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // get department information
        DepartmentEntity department = departmentService.getDepartment(Convert.toLong(value));
        if (department == null) {
            log.warn("[apply][get department {{}} is empty", value);
            return "";
        }
        return department.getName();
    }

}
