package ru.sberbank.pprb.sbbol.partners.audit.model;

/**
 * Типы событий доступных для отправки в Audit.
 */
public enum EventType {

    ACCOUNT_CREATE_SUCCESS("account_create_success"),
    ACCOUNT_CREATE_ERROR("account_create_error"),
    ACCOUNT_UPDATE_SUCCESS("account_update_success"),
    ACCOUNT_UPDATE_ERROR("account_update_error"),
    ACCOUNT_DELETE_SUCCESS("account_delete_success"),
    ACCOUNT_DELETE_ERROR("account_delete_error"),
    SIGN_ACCOUNT_CREATE_SUCCESS("sign_account_create_success"),
    SIGN_ACCOUNT_CREATE_ERROR("sign_account_create_error"),
    SIGN_ACCOUNT_DELETE_SUCCESS("sign_account_delete_success"),
    SIGN_ACCOUNT_DELETE_ERROR("sign_account_delete_error");

    private final String desc;

    EventType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
