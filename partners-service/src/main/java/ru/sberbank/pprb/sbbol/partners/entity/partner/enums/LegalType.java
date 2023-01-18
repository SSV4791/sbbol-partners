package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

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

    public static LegalType of(LegalForm form) {
        return switch (form) {
            case PHYSICAL_PERSON -> LegalType.PHYSICAL_PERSON;
            case LEGAL_ENTITY -> LegalType.LEGAL_ENTITY;
            case ENTREPRENEUR -> LegalType.ENTREPRENEUR;
        };
    }
}
