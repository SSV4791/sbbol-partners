package ru.sberbank.pprb.sbbol.partners.partners.handler;

public enum ErrorCode {

    MODEL_VALIDATION_EXCEPTION(21),
    EXCEPTION(22),
    MODEL_DUPLICATE_EXCEPTION(23),
    OPTIMISTIC_LOCK_EXCEPTION(24),
    MODEL_NOT_FOUND_EXCEPTION(25),
    ENTRY_SAVE_EXCEPTION(28);

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
