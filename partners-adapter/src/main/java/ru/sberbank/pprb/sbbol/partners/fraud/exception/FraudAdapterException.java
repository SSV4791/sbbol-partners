package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import static java.lang.String.format;

public class FraudAdapterException extends RuntimeException {

    private static final String ERROR_MESSAGE =
        "Ошибка при выполнении http-запроса к АС Интегратор ФП ЮЛ. Сообщение об ошибке: %s";

    public FraudAdapterException(String message) {
        super(format(ERROR_MESSAGE, message));
    }
    public FraudAdapterException(String message, Throwable cause) {
        super(format(ERROR_MESSAGE, message), cause);
    }
}
