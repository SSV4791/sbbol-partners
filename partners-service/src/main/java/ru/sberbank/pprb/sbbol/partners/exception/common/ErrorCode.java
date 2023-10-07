package ru.sberbank.pprb.sbbol.partners.exception.common;

public enum ErrorCode {

    MODEL_VALIDATION_EXCEPTION(21),
    EXCEPTION(22),
    ACCOUNT_DUPLICATE_EXCEPTION(23),
    PARTNER_DUPLICATE_EXCEPTION(231),
    EXTERNAL_ID_DUPLICATE_EXCEPTION(232),
    MODEL_DUPLICATE_EXCEPTION(233),
    OPTIMISTIC_LOCK_EXCEPTION(24),
    ACCOUNT_ALREADY_SIGNED_EXCEPTION(25),
    PRIORITY_ACCOUNT_MORE_ONE(26),
    MODEL_NOT_FOUND_EXCEPTION(27),
    ENTRY_SAVE_EXCEPTION(28),
    MULTIPLE_FOUND_EXCEPTION(222),
    FRAUD_MODEL_VALIDATION_EXCEPTION(291),
    FRAUD_DENIED_EXCEPTION(292),
    NOT_FOUND_REPLICATION_ENTITY_MAPPER_EXCEPTION(293);

    private final Integer value;

    ErrorCode(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
