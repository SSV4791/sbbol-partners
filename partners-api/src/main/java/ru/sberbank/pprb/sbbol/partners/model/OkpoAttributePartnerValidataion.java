package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.partner.okpo.OkpoLengthPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.okpo.OkpoLengthPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.okpo.OkpoLengthPartnerDtoValidator;

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
        OkpoLengthPartnerDtoValidator.class,
        OkpoLengthPartnerCreateDtoValidator.class,
        OkpoLengthPartnerCreateFullModelDtoValidator.class
    }
)
public @interface OkpoAttributePartnerValidataion {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
