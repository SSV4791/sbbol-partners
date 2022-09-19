package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.common.BaseException;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.List;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.PRIORITY_ACCOUNT_MORE_ONE;

public class AccountPriorityOneMoreException extends BaseException {

    private static final String EXCEPTION = "PPRB:ACCOUNT:PRIORITY_ONE_MORE_EXCEPTION";
    private static final String FIELD = "account";
    private static final String LOG_MESSAGE = "У контрагента уже есть приоритетный счёт";

    public AccountPriorityOneMoreException(String digitalId, UUID partnerId) {
        super(
            Error.TypeEnum.BUSINESS,
            EXCEPTION,
            FIELD,
            List.of(MessagesTranslator.toLocale("account.account.sign.is_true", digitalId, partnerId)),
            PRIORITY_ACCOUNT_MORE_ONE,
            LOG_MESSAGE
        );
    }
}
