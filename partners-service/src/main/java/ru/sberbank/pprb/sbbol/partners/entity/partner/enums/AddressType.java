package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Тип адреса Партнера
 */
public enum AddressType {

    LEGAL_ADDRESS("Юридический адрес"),
    PHYSICAL_ADDRESS("Физический адрес"),
    REGISTRATION_ADDRESS("Адрес регистрации ФЛ"),
    RESIDENTIAL_ADDRESS("Адрес проживания")
    ;

    private final String desc;

    AddressType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
