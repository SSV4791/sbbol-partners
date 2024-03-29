package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.bankaccount.BankAccountAttributeKeyBankChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.bankaccount.BankAccountAttributeKeyBankDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.bankaccount.BankAccountAttributeKeyBankCreateDtoValidator;

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
        BankAccountAttributeKeyBankDtoValidator.class,
        BankAccountAttributeKeyBankCreateDtoValidator.class,
        BankAccountAttributeKeyBankChangeDtoValidator.class
    }
)
public @interface BankAccountValidation {

    String message() default "{account.account.bank_account.control_number}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
