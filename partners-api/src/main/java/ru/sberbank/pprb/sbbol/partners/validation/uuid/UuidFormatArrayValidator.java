package ru.sberbank.pprb.sbbol.partners.validation.uuid;

import ru.sberbank.pprb.sbbol.partners.model.UuidFormatArrayValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

public class UuidFormatArrayValidator extends BaseUuidFormatValidator
    implements ConstraintValidator<UuidFormatArrayValidation, List<String>> {

    private String message;

    @Override
    public void initialize(UuidFormatArrayValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        var result = true;
        var count = 0;
        for (var id : value) {
            if (uuidIsNotValid(id)) {
                result = false;
                buildMessage(context, count, message);
            }
            count++;
        }
        return result;
    }
}
