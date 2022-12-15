package ru.sberbank.pprb.sbbol.partners.exception.common;

public enum ErrorCode {

    MODEL_VALIDATION_EXCEPTION(21),
    EXCEPTION(22),
    MODEL_DUPLICATE_EXCEPTION(23),
    OPTIMISTIC_LOCK_EXCEPTION(24),
    ACCOUNT_ALREADY_SIGNED_EXCEPTION(25),
    PRIORITY_ACCOUNT_MORE_ONE(26),
    MODEL_NOT_FOUND_EXCEPTION(27),
    ENTRY_SAVE_EXCEPTION(28),
    FRAUD_DENIED_EXCEPTION(29),
    FRAUD_MODEL_VALIDATION_EXCEPTION(30);

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
