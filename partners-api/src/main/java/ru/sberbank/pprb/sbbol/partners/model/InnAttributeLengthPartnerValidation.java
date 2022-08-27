package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.InnAttributeLengthPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.InnAttributeLengthPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.InnAttributeLengthPartnerDtoValidator;

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
public @interface InnAttributeLengthPartnerValidation {

    String message() default "{partner.inn_length}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
