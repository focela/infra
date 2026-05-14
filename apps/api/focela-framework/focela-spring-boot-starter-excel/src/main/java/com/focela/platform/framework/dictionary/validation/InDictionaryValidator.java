package com.focela.platform.framework.dictionary.validation;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.dictionary.core.DictionaryFrameworkUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class InDictionaryValidator implements ConstraintValidator<InDictionary, Object> {

    private String dictType;

    @Override
    public void initialize(InDictionary annotation) {
        this.dictType = annotation.type();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // When empty, skip validation by default (treat as passing)
        if (value == null) {
            return true;
        }
        // Validation passes
        final List<String> values = DictionaryFrameworkUtils.getDictDataValueList(dictType);
        boolean match = values.stream().anyMatch(v -> StrUtil.equalsIgnoreCase(v, value.toString()));
        if (match) {
            return true;
        }

        // Validation failed; build a custom error message
        context.disableDefaultConstraintViolation(); // Disable the default message value
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate().replaceAll("\\{value}", values.toString())
        ).addConstraintViolation(); // Re-add the error message
        return false;
    }

}

