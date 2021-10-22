package ru.sberbank.pprb.sbbol.partners.entity;//

/**
 * Тип сущности Партнер
 */
public enum RenterType {

    LEGAL_ENTITY("Юр. лицо"),
    ENTREPRENEUR("ИП"),
    PHYSICAL_PERSON("Физ. лицо"),
    ;

    private final String desc;


    RenterType(String desc) {
        this.desc = desc;

    }

    public String getDesc() {
        return desc;
    }

}
