package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.accounts.AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
@Constraint(
    validatedBy = {
        AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator.class,
        AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator.class,
        AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator.class
    }
)
public @interface AccountAttributeBalanceValidation {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
