package ru.sberbank.pprb.sbbol.partners.audit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.sberbank.pprb.sbbol.audit.model.BaseResponse;

/**
 * Ошибка, при работе с сервисом Audit
 */
public class AuditSendException extends RuntimeException {

    private static final String EXCEPTION = "AUDIT:PARTNER:ADAPTER_EXCEPTION";

    private final String code;

    public AuditSendException(HttpStatus code, ResponseEntity<BaseResponse> response) {
        super("Ошибка при работе с сервисом Audit: StatusCode: " + code + "Message: " + response);
        this.code = EXCEPTION;
    }

    public String getCode() {
        return code;
    }
}
