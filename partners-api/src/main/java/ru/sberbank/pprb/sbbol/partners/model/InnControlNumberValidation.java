package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnControlNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = InnControlNumberValidator.class)
public @interface InnControlNumberValidation {

    String message() default "{validation.partner.inn.control_number}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
