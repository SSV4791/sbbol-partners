package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeRurCodeCurrencyAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeRurCodeCurrencyAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidator;

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
        AccountAttributeRurCodeCurrencyAccountChangeDtoValidator.class,
        AccountAttributeRurCodeCurrencyAccountCreateDtoValidator.class,
        AccountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidator.class,
    }
)
public @interface AccountRubCodeCurrencyValidation {

    String message() default "{validation.account.rub_code_currency}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
