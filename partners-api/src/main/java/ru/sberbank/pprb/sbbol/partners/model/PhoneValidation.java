package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.PhoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PhoneValidator.class)
public @interface PhoneValidation {

    String message() default "{phone.phone_valid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
