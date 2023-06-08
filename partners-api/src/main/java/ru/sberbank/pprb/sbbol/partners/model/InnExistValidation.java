package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeExistPartnerChangeFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeExistPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeExistPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.inn.InnAttributeExistPartnerDtoValidator;

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
        InnAttributeExistPartnerCreateFullModelDtoValidator.class,
        InnAttributeExistPartnerChangeFullModelDtoValidator.class
    }
)
public @interface InnExistValidation {

    String message() default "{javax.validation.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
