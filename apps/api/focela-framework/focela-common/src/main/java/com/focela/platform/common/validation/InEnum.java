package com.focela.platform.common.validation;

import com.focela.platform.common.core.ArrayValuable;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {InEnumValidator.class, InEnumCollectionValidator.class}
)
public @interface InEnum {

    /**
     * @return class implementing the ArrayValuable interface
     */
    Class<? extends ArrayValuable<?>> value();

    String message() default "must be within the specified range {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
