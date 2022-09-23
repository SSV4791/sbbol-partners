package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;

public class OptimisticLockException extends BaseException {

    private static final String EXCEPTION = "PPRB:PARTNER:OPTIMISTIC_LOCK_EXCEPTION";
    private static final String FIELD = "version";
    private static final String LOG_MESSAGE = "Ошибка заполнения объекта";

    public OptimisticLockException(Long foundVersion, Long version) {
        super(
            Error.TypeEnum.BUSINESS,
            EXCEPTION,
            FIELD,
            List.of(MessagesTranslator.toLocale("default.fields.object_version_error", foundVersion, version)),
            OPTIMISTIC_LOCK_EXCEPTION,
            LOG_MESSAGE
        );
    }
}
