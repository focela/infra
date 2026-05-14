package com.focela.platform.framework.common.validation;

import com.focela.platform.framework.common.core.ArrayValuable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    private List<?> values;

    @Override
    public void initialize(InEnum annotation) {
        ArrayValuable<?>[] values = annotation.value().getEnumConstants();
        if (values.length == 0) {
            this.values = Collections.emptyList();
        } else {
            this.values = Arrays.asList(values[0].array());
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // when null, skip validation by default (i.e., consider it passed)
        if (value == null) {
            return true;
        }
        // validation passed
        if (values.contains(value)) {
            return true;
        }
        // validation failed, customize the error message
        context.disableDefaultConstraintViolation(); // disable the default message value
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                .replaceAll("\\{value}", values.toString())).addConstraintViolation(); // re-add the error message
        return false;
    }

}

