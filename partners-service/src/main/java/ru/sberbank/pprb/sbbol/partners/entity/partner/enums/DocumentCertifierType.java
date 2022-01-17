package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Тип удостоверяющего сотрудника
 */
public enum DocumentCertifierType {

    UNKNOWN("Не определено"),
    NOTARY("Нотариус"),
    ;

    private final String desc;

    DocumentCertifierType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
