package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.NameAttributePartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.NameAttributePartnerDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.NameAttributePartnerFullModelDtoValidator;

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
        NameAttributePartnerDtoValidator.class,
        NameAttributePartnerCreateDtoValidator.class,
        NameAttributePartnerFullModelDtoValidator.class
    }
)
public @interface NameAttributePartnerValidation {

    String message() default "{partner.legal_form}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
