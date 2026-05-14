package com.focela.platform.framework.common.validation;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.utils.validation.ValidationUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<Mobile, String> {

    @Override
    public void initialize(Mobile annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // if mobile is empty, skip validation by default (i.e., pass)
        if (StrUtil.isEmpty(value)) {
            return true;
        }
        // validate mobile
        return ValidationUtils.isMobile(value);
    }

}
