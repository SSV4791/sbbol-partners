package ru.sberbank.pprb.sbbol.partners.validation;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BasePatternPartnerValidator {
    private static final String MESSAGE_ORG_NAME = "{validation.partner.orgName.illegal_symbols}";
    private static final String MESSAGE_FIRST_NAME = "{validation.partner.firstName.illegal_symbols}";
    private static final String MESSAGE_SECOND_NAME = "{validation.partner.lastName.illegal_symbols}";
    private static final String MESSAGE_MIDDLE_NAME = "{validation.partner.middleName.illegal_symbols}";
    private static final String REGEX = "[A-Za-zА-Яа-я0-9Ёё!\\\"№#$%&'() *+,-./:;<=>?@\\\\[\\\\\\\\\\\\]^_`{|}~\\\\r\\\\n]+";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    protected boolean isValid(ConstraintValidatorContext context,
                              String orgName, String firstName, String secondName, String middleName) {
        context.disableDefaultConstraintViolation();
        if(!isEmpty(orgName)) {
            boolean isValidOrgName = PATTERN.matcher(orgName).matches();
            if (!isValidOrgName) {
                buildMessage(context, "orgName", MESSAGE_ORG_NAME, getInvalidChars(orgName));
                return false;
            }
        }
        if(!isEmpty(firstName)) {
            boolean isValidFirstName = PATTERN.matcher(firstName).matches();
            if (!isValidFirstName) {
                buildMessage(context, "firstName", MESSAGE_FIRST_NAME, getInvalidChars(firstName));
                return false;
            }
        }
        if(!isEmpty(secondName)) {
            boolean isValidSecondName = PATTERN.matcher(secondName).matches();
            if (!isValidSecondName) {
                buildMessage(context, "secondName", MESSAGE_SECOND_NAME, getInvalidChars(secondName));
                return false;
            }
        }
        if(!isEmpty(middleName)) {
            boolean isValidMiddleName = PATTERN.matcher(middleName).matches();
            if (!isValidMiddleName) {
                buildMessage(context, "middleName", MESSAGE_MIDDLE_NAME, getInvalidChars(middleName));
                return false;
            }
        }
        return true;
    }

    private void buildMessage(ConstraintValidatorContext context, String field, String message, String invalidChars) {
        var builder = context.buildConstraintViolationWithTemplate(message + " " + invalidChars);
        builder
            .addPropertyNode(field)
            .addConstraintViolation();
    }

    private String getInvalidChars(String field) {
        var invalidCharsArray = PATTERN.split(field);
        var invalidChars = new StringBuilder();
        Arrays.stream(invalidCharsArray).forEach(invalidChars::append);
        return invalidChars.toString();
    }
}
