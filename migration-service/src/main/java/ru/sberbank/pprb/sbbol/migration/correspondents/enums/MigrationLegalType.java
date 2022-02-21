package ru.sberbank.pprb.sbbol.migration.correspondents.enums;

/**
 * Тип правовой формы Партнера
 */
public enum MigrationLegalType {

    LEGAL_ENTITY("Юридическое лицо"),
    ENTREPRENEUR("Индивидуальный предприниматель"),
    PHYSICAL_PERSON("Физическое лицо"),
    ;

    private final String desc;

    MigrationLegalType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
