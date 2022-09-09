package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeLengthPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeLengthPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeLengthPartnerDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(
    validatedBy = {
        InnAttributeLengthPartnerDtoValidator.class,
        InnAttributeLengthPartnerCreateDtoValidator.class,
        InnAttributeLengthPartnerCreateFullModelDtoValidator.class
    }
)
public @interface InnLengthValidation {

    String message() default "{validation.partner.inn.length}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
