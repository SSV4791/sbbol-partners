package ru.sberbank.pprb.sbbol.migration.correspondents.enums;

/**
 * Тип Партнера
 */
public enum MigrationPartnerType {

    RENTER("Арендатор"),
    PARTNER("Партнер"),
    BENEFICIARY("Бенефициар"),
    ;

    private final String desc;

    MigrationPartnerType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
