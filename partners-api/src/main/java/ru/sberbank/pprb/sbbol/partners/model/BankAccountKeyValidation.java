package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.account.BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateFullModelDtoValidator;

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
        BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeDtoValidator.class,
        BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateDtoValidator.class,
        BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateFullModelDtoValidator.class,
        BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeFullModelDtoValidator.class
    }
)
public @interface BankAccountKeyValidation {

    String message() default "{validation.partner.bank_account.not_person_or_entrepreneur_entity_format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
