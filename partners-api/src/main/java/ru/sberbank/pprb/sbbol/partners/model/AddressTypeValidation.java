package ru.sberbank.pprb.sbbol.partners.model;

import ru.sberbank.pprb.sbbol.partners.validation.address.AddressTypeAttributeAddressCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.address.AddressTypeAttributeAddressDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.address.AddressTypeAttributePartnerChangeFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.address.AddressTypeAttributePartnerCreateFullModelDtoValidator;

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
        AddressTypeAttributeAddressCreateDtoValidator.class,
        AddressTypeAttributeAddressDtoValidator.class,
        AddressTypeAttributePartnerChangeFullModelDtoValidator.class,
        AddressTypeAttributePartnerCreateFullModelDtoValidator.class
    }
)
public @interface AddressTypeValidation {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
