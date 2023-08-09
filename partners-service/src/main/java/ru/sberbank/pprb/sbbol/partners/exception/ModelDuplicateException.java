package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.Collections;

import static java.lang.String.format;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_DUPLICATE_EXCEPTION;

public class ModelDuplicateException extends BaseException {

    private static final String LOG_MESSAGE = "Зафиксирована попытка создания дубликата сущности: %s";

    public ModelDuplicateException(String entityName) {
        super(
            Error.TypeEnum.BUSINESS,
            MessagesTranslator.toLocale("error.message.check.validation"),
            Collections.emptyMap(),
            MODEL_DUPLICATE_EXCEPTION,
            format(LOG_MESSAGE, entityName)
        );
    }
}
