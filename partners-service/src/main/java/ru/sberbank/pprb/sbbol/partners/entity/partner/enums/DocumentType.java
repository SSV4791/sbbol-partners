package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Тип документа Партнера
 */
public enum DocumentType {

    PASSPORT_OF_RUSSIA("Паспорт гражданина РФ"),
    SEAMAN_PASSPORT("Паспорт моряка (удостоверение личности моряка)"),
    SERVICEMAN_IDENTITY_CARD_OF_RUSSIA("Удостоверение личности военнослужащего"),
    FOREIGN_PASSPORT("Паспорт иностранного гражданина"),
    SERVICE_PASSPORT_OF_RUSSIA("Служебный паспорт гражданина РФ"),
    RF_CITIZEN_DIPLOMATIC_PASSPORT("Дипломатический паспорт"),
    PASSPORT_OF_RUSSIA_WITH_CHIP("Паспорт гражданина РФ, содержащий электронный носитель информации"),
    ;

    private final String desc;

    DocumentType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
