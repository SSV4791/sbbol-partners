package ru.sberbank.pprb.sbbol.partners.exception.common;

import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseException extends RuntimeException {

    private final Error.TypeEnum errorType;
    private final ErrorCode errorCode;
    private final String text;
    private final Map<String, List<String>> errors = new HashMap<>();
    private final String logMessage;

    public BaseException(Error.TypeEnum errorType, String text, String field, List<String> errors, ErrorCode errorCode, String logMessage) {
        this.errorType = errorType;
        this.text = text;
        this.errorCode = errorCode;
        this.logMessage = logMessage;
        this.errors.put(field, errors);
    }

    public BaseException(Error.TypeEnum errorType, String text, Map<String, List<String>> errors, ErrorCode errorCode, String logMessage) {
        this.errorType = errorType;
        this.text = text;
        this.errorCode = errorCode;
        this.logMessage = logMessage;
        this.errors.putAll(errors);
    }

    public String getText() {
        return text;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public Error.TypeEnum getType() {
        return errorType;
    }

    public Integer getErrorCodeValue() {
        return errorCode.getValue();
    }

    public String getLogMessage() {
        return logMessage;
    }
}
