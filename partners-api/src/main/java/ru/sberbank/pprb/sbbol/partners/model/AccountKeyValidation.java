package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeKeyAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeKeyAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeKeyAccountCreateFullModelDtoValidator;

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
@Constraint(
    validatedBy = {
        AccountAttributeKeyAccountChangeDtoValidator.class,
        AccountAttributeKeyAccountCreateDtoValidator.class,
        AccountAttributeKeyAccountCreateFullModelDtoValidator.class,
    }
)
public @interface AccountKeyValidation {

    String message() default "{validation.account.control_number}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
