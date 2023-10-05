package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.Collections;
import java.util.UUID;

import static java.lang.String.format;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.EXTERNAL_ID_DUPLICATE_EXCEPTION;

public class CheckDuplicateException extends BaseException {

    private static final String LOG_MESSAGE = "Зафиксирована попытка создания дубликата сущности: %s";

    public CheckDuplicateException(UUID externalId) {
        super(
            Error.TypeEnum.BUSINESS,
            MessagesTranslator.toLocale("external_id.duplicate", externalId),
            Collections.emptyMap(),
            EXTERNAL_ID_DUPLICATE_EXCEPTION,
            format(LOG_MESSAGE, "externalId")
        );
    }

    public CheckDuplicateException(ErrorCode errorCode) {
        super(
            Error.TypeEnum.BUSINESS,
            getMessage(errorCode),
            Collections.emptyMap(),
            errorCode,
            format(LOG_MESSAGE, getEntityName(errorCode))
        );
    }

    private static String getEntityName(ErrorCode errorCode) {
        return switch (errorCode) {
            case ACCOUNT_DUPLICATE_EXCEPTION -> "account";
            case PARTNER_DUPLICATE_EXCEPTION -> "partner";
            default -> "Невозможно определить сущность, передан неверный ErrorCode";
        };
    }

    private static String getMessage(ErrorCode errorCode) {
        return switch (errorCode) {
            case ACCOUNT_DUPLICATE_EXCEPTION -> MessagesTranslator.toLocale("account.duplicate");
            case PARTNER_DUPLICATE_EXCEPTION -> MessagesTranslator.toLocale("partner.duplicate");
            default -> "Невозможно определить сущность, передан неверный ErrorCode";
        };
    }
}
