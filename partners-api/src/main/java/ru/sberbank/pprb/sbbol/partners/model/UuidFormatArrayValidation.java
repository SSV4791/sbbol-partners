package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.uuid.UuidFormatArrayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Documented
@Constraint(
    validatedBy = {
        UuidFormatArrayValidator.class
    }
)
public @interface UuidFormatArrayValidation {

    String message() default "{validation.uuid_id.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
