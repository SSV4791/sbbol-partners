package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Тип адреса Партнера
 */
public enum AddressType {

    LEGAL_ADDRESS("Юридический адрес"),
    PHYSICAL_ADDRESS("Физический адрес"),
    ;

    private final String desc;

    AddressType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
