package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Признак /"Гражданин РФ/", заполняется для физ. лиц
 */
public enum PartnerCitizenshipType {

    UNKNOWN("Не определено"),
    RUSSIA("Россия"),
    ANOTHER_COUNTRY("Другая страна"),
    ;

    private final String desc;

    PartnerCitizenshipType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
