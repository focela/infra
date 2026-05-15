package com.focela.platform.dictionary.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
        validatedBy = {InDictionaryValidator.class, InDictionaryCollectionValidator.class}
)
public @interface InDictionary {

    /**
     * Dictionary data type.
     */
    String type();

    String message() default "must be within the specified range {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
