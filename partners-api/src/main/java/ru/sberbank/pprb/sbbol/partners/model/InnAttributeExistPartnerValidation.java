package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.InnAttributeExistPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.InnAttributeExistPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.InnAttributeExistPartnerDtoValidator;

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
        InnAttributeExistPartnerDtoValidator.class,
        InnAttributeExistPartnerCreateDtoValidator.class,
        InnAttributeExistPartnerCreateFullModelDtoValidator.class
    }
)
public @interface InnAttributeExistPartnerValidation {

    String message() default "{validation.partner.inn.is_null}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
