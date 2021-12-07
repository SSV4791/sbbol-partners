package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Признак подписанности.
 */
public enum AccountStateType {

    SIGNED("Подписан"),
    NOT_SIGNED("Не подписан"),
    ;

    private final String desc;

    AccountStateType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
