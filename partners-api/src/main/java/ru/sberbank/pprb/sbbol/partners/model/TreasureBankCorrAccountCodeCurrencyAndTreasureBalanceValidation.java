package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeTreasureBankCorrAccountAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeTreasureBankCorrAccountAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeTreasureBankCorrAccountAccountCreateFullModelDtoValidator;

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
        AccountAttributeTreasureBankCorrAccountAccountChangeDtoValidator.class,
        AccountAttributeTreasureBankCorrAccountAccountCreateDtoValidator.class,
        AccountAttributeTreasureBankCorrAccountAccountCreateFullModelDtoValidator.class,
    }
)
public @interface TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation {

    String message() default "{validation.partner.bank_account.not_legal_entity_format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
