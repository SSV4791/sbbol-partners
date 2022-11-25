package ru.sberbank.pprb.sbbol.partners.model.fraud;

public enum FraudEventType {

    SIGN_ACCOUNT("SIGN_ACCOUNT"),

    DELETE_PARTNER("DELETE_PARTNER");

    private String value;

    FraudEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
