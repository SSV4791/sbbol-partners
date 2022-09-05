package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.OkpoPartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.OkpoPartnerCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.OkpoPartnerDtoValidator;

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
        OkpoPartnerDtoValidator.class,
        OkpoPartnerCreateDtoValidator.class,
        OkpoPartnerCreateFullModelDtoValidator.class
    }
)
public @interface OkpoValidataion {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
