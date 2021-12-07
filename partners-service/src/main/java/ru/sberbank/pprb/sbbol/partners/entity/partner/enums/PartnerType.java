package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Тип Партнера
 */
public enum PartnerType {

    RENTER("Арендатор"),
    PARTNER("Партнер"),
    BENEFICIARY("Бенефициар"),
    ;

    private final String desc;

    PartnerType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
