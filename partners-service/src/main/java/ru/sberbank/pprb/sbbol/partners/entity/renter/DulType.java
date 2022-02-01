package ru.sberbank.pprb.sbbol.partners.entity.renter;

/**
 * Тип удостоверяющего документа (физ. лицо)
 */
@Deprecated
public enum DulType {

    PASSPORTOFRUSSIA("Паспорт гражданина РФ"),
    SEAMANPASSPORT("Паспорт моряка (удостоверение личности моряка)"),
    SERVICEMANIDENTITYCARDOFRUSSIA("Удостоверение личности военнослужащего"),
    FOREIGNPASSPORT("Паспорт иностранного гражданина"),
    SERVICEPASSPORTOFRUSSIA("Служебный паспорт гражданина РФ"),
    RFCITIZENDIPLOMATICPASSPORT("Дипломатический паспорт"),
    PASSPORTOFRUSSIAWITHCHIP("Паспорт гражданина РФ, содержащий электронный носитель информации"),
    ;

    private final String desc;


    DulType(String desc) {
        this.desc = desc;

    }

    public String getDesc() {
        return desc;
    }
}
