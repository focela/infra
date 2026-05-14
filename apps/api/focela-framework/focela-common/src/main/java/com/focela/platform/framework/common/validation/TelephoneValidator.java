package com.focela.platform.framework.common.validation;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelephoneValidator implements ConstraintValidator<Telephone, String> {

    @Override
    public void initialize(Telephone annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // if mobile is empty, skip validation by default (i.e., pass)
        if (CharSequenceUtil.isEmpty(value)) {
            return true;
        }
        // validate mobile
        return PhoneUtil.isTel(value) || PhoneUtil.isPhone(value);
    }

}
