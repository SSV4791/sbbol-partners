package ru.sberbank.pprb.sbbol.partners.validation.uuid;

import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import java.util.regex.Pattern;

public class BaseUuidFormatValidator extends BaseValidator {

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    protected boolean uuidIsNotValid(String id) {
        return !UUID_PATTERN.matcher(id).matches();
    }
}
