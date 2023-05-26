package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalanceTreasureAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalanceTreasureAccountChangeFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalanceTreasureAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalanceTreasureAccountCreateFullModelDtoValidator;

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
        AccountAttributeBalanceTreasureAccountChangeDtoValidator.class,
        AccountAttributeBalanceTreasureAccountCreateDtoValidator.class,
        AccountAttributeBalanceTreasureAccountCreateFullModelDtoValidator.class,
        AccountAttributeBalanceTreasureAccountChangeFullModelDtoValidator.class
    }
)
public @interface BalanceTreasureAccountValidation {

    String message() default "{validation.account.treasure_balance}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
