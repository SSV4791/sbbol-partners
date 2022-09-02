package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.OgrnAttributeLengthPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.OgrnAttributeLengthPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.OgrnAttributeLengthPartnerDtoValidator;

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
        OgrnAttributeLengthPartnerDtoValidator.class,
        OgrnAttributeLengthPartnerCreateDtoValidator.class,
        OgrnAttributeLengthPartnerCreateFullModelDtoValidator.class
    }
)
public @interface OgrnAttributeLengthPartnerValidation {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
