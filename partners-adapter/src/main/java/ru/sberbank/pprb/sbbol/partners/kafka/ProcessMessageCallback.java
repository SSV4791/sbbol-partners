package ru.sberbank.pprb.sbbol.partners.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Callback для обработки результатов отправки сообщения в топик кафки.
 *
 * @param <T> тип сообщения
 */
public class ProcessMessageCallback<T> implements ListenableFutureCallback<SendResult<String, T>> {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessMessageCallback.class);

    private final T message;

    public ProcessMessageCallback(T message) {
        this.message = message;
    }

    @Override
    public void onFailure(@NonNull Throwable ex) {
        LOG.error("Сообщение \"{}\" не отправлено в топик кафки", message);
    }

    @Override
    public void onSuccess(SendResult<String, T> result) {
        if (result != null) {
            LOG.debug("Сообщение \"{}\" отправлено, offset = {}", message, result.getRecordMetadata().offset());
        } else {
            LOG.debug("Сообщение \"{}\" отправлено, result = null", message);
        }
    }
}
