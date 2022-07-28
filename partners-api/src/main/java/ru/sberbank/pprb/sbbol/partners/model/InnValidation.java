package ru.sberbank.pprb.sbbol.partners.model;


import ru.sberbank.pprb.sbbol.partners.validation.InnValidator;

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
@Constraint(validatedBy = InnValidator.class)
public @interface InnValidation {

    String message() default "{partner.inn.control_number}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
