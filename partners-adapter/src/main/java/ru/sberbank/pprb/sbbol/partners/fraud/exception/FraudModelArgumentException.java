package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import static java.lang.String.format;

public class FraudModelArgumentException extends RuntimeException {

    private static final String ERROR_MESSAGE =
        "Ошибка валидации модели при выполнении http-запроса к АС Интегратор ФП ЮЛ. Сообщение об ошибке: %s";

    public FraudModelArgumentException(String message, Throwable cause) {
        super(format(ERROR_MESSAGE, message), cause);
    }
}
