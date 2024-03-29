package ru.sberbank.pprb.sbbol.partners.validation.partner.kpp;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.KppValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class KppValidator extends BaseValidator
    implements ConstraintValidator<KppValidation, String> {

    private static final Pattern PATTERN = Pattern.compile("[0-9a-zA-Zа-яА-ЯЁё№! \"#$%&'()*+,-./:;<=>?@\\^_`{|}~\n\r]{9}");
    private static final String MESSAGE = "{validation.partner.kpp.format}";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value) || "0".equals(value)) {
            return true;
        }
        buildMessage(context, MESSAGE);
        return PATTERN.matcher(value).matches();
    }
}
