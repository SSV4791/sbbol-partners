package ru.sberbank.pprb.sbbol.partners.validation.common;

import javax.validation.ConstraintValidatorContext;

public class BaseValidator {

    public void buildMessage(ConstraintValidatorContext context, String field, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(field)
            .addConstraintViolation();
    }

    public void buildMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation();
    }

    public void buildMessage(ConstraintValidatorContext context, int i, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addBeanNode()
            .inIterable()
            .atIndex(i)
            .addConstraintViolation();
    }
}
