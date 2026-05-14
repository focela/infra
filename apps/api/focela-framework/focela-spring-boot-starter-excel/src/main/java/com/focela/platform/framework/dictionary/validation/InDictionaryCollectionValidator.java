package com.focela.platform.framework.dictionary.validation;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.dictionary.core.DictionaryFrameworkUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.List;

public class InDictionaryCollectionValidator implements ConstraintValidator<InDictionary, Collection<?>> {

    private String dictType;

    @Override
    public void initialize(InDictionary annotation) {
        this.dictType = annotation.type();
    }

    @Override
    public boolean isValid(Collection<?> list, ConstraintValidatorContext context) {
        // When empty, skip validation by default (treat as passing)
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        // All values pass validation
        List<String> dbValues = DictionaryFrameworkUtils.getDictDataValueList(dictType);
        boolean match = list.stream().allMatch(v -> dbValues.stream()
                .anyMatch(dbValue -> dbValue.equalsIgnoreCase(v.toString())));
        if (match) {
            return true;
        }

        // Validation failed; build a custom error message
        context.disableDefaultConstraintViolation(); // Disable the default message value
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate().replaceAll("\\{value}", dbValues.toString())
        ).addConstraintViolation(); // Re-add the error message
        return false;
    }

}

