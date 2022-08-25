package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.NamePatternPartnerCreateDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NamePatternPartnerCreateDtoValidator.class)
public @interface NamePatternPartnerCreateDtoValidation {
    String message() default "{error.message.check.validation}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
