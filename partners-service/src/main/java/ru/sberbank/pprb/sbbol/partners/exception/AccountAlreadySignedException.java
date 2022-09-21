package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.ACCOUNT_ALREADY_SIGNED_EXCEPTION;

public class AccountAlreadySignedException extends BaseException {

    private static final String EXCEPTION = "PPRB:ACCOUNT:ALREADY_SIGNED_EXCEPTION";
    private static final String FIELD = "account";
    private static final String LOG_MESSAGE = "Зафиксирована попытка повторной подписи или изменения подписанного счёта";

    public AccountAlreadySignedException(String account) {
        super(
            Error.TypeEnum.BUSINESS,
            EXCEPTION,
            FIELD,
            List.of(MessagesTranslator.toLocale("account.account.sign.is_true", account)),
            ACCOUNT_ALREADY_SIGNED_EXCEPTION,
            LOG_MESSAGE
        );
    }
}
