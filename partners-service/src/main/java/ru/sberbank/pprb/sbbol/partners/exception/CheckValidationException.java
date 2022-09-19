package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.List;
import java.util.Map;

import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;

public class CheckValidationException extends BaseException {

    private static final String EXCEPTION = "PPRB:PARTNER:CHECK_VALIDATION_EXCEPTION";
    private static final String LOG_MESSAGE = "Ошибка заполнения объекта";

    public CheckValidationException(Map<String, List<String>> errors) {
        super(
            Error.TypeEnum.BUSINESS,
            EXCEPTION,
            errors,
            MODEL_VALIDATION_EXCEPTION,
            LOG_MESSAGE
        );
    }
}
