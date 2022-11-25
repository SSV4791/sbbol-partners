package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import static java.lang.String.format;

public class FraudApplicationException extends RuntimeException {

    private static final String ERROR_MESSAGE =
        "Ошибка в работе приложения со стороны АС Интегратор ФП ЮЛ. Сообщение об ошибке: %s";

    public FraudApplicationException(String message, Throwable cause) {
        super(format(ERROR_MESSAGE, message), cause);
    }
}
