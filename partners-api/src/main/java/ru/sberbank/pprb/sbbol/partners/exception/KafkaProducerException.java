package ru.sberbank.pprb.sbbol.partners.exception;

public class KafkaProducerException extends Exception {

    private static final String ERROR_PREFIX = "Невозможно отправить сообщение: ";

    public KafkaProducerException(String message, Throwable cause) {
        super(ERROR_PREFIX.concat(message), cause);
    }
}
