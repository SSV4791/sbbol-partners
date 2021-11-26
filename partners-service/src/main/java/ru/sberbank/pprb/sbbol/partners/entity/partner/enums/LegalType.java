package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Тип правовой формы Партнера
 */
public enum LegalType {

    LEGAL_ENTITY("Юридическое лицо"),
    ENTREPRENEUR("Индивидуальный предприниматель"),
    PHYSICAL_PERSON("Физическое лицо"),
    ;

    private final String desc;

    LegalType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
