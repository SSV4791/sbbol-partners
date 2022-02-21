package ru.sberbank.pprb.sbbol.migration.correspondents.enums;

/**
 * Признак /"Гражданин РФ/", заполняется для физ. лиц
 */
public enum MigrationPartnerCitizenshipType {

    UNKNOWN("Не определено"),
    RUSSIA("Россия"),
    ANOTHER_COUNTRY("Другая страна"),
    ;

    private final String desc;

    MigrationPartnerCitizenshipType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
