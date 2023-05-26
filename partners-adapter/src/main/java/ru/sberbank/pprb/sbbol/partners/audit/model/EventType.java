package ru.sberbank.pprb.sbbol.partners.audit.model;

/**
 * Типы событий доступных для отправки в Audit.
 */
public enum EventType {

    ACCOUNT_CREATE("account_create_success", "account_create_error"),
    ACCOUNT_UPDATE("account_update_success", "account_update_error"),
    ACCOUNTS_DELETE("accounts_delete_success", "accounts_delete_error"),
    SIGN_ACCOUNTS_CREATE("sign_accounts_create_success", "sign_accounts_create_error"),
    SIGN_ACCOUNTS_DELETE("sign_accounts_delete_success", "sign_accounts_delete_error"),
    PARTNER_FULL_MODEL_CREATE("partner_full_model_create_success", "partner_full_model_create_error"),
    PARTNER_FULL_MODEL_UPDATE("partner_full_model_update_success", "partner_full_model_update_error"),
    PARTNER_CREATE("partner_create_success", "partner_create_error"),
    PARTNER_UPDATE("partner_update_success", "partner_update_error"),
    PARTNER_DELETE("partner_delete_success", "partner_delete_error");

    private final String successEventName;
    private final String errorEventName;

    EventType(String successEventName, String errorEventName) {
        this.successEventName = successEventName;
        this.errorEventName = errorEventName;
    }

    public String getSuccessEventName() {
        return successEventName;
    }

    public String getErrorEventName() {
        return errorEventName;
    }
}
