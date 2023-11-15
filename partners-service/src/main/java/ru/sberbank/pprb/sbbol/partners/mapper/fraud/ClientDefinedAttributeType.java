package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.DataType.STRING;

public enum ClientDefinedAttributeType {

    RECEIVER_NAME("Наименование получателя", STRING),
    COUNTER_PARTY_ID("Уникальный идентификатор партнера", STRING),
    USER_COMMENT("Комментарий пользователя", STRING),
    RECEIVER_INN("ИНН получателя", STRING),
    PAYER_INN("ИНН отправителя", STRING),
    RECEIVER_BIC_SWIFT("БИК SWIFT получателя", STRING),
    RECEIVER_ACCOUNT("Номер счета получателя", STRING),
    OSB_NUMBER("Номер ОСБ", STRING),
    VSP_NUMBER("Номер ВСП", STRING),
    DBO_OPERATION("Операция ДБО", STRING),
    PAYER_NAME("Наименование клиента", STRING),
    FIRST_SIGN_TIME("1-я подпись Время подписи", DataType.DATE),
    FIRST_SIGN_IP_ADDRESS("1-я подпись IP адрес", STRING),
    FIRST_SIGN_LOGIN("1-я подпись Логин", STRING),
    FIRST_SIGN_CRYPTOPROFILE("1-я подпись Наименование криптопрофиля", STRING),
    FIRST_SIGN_CRYPTOPROFILE_TYPE("1-я подпись Тип криптопрофиля", STRING),
    FIRST_SIGN_CHANNEL("1-я подпись Канал подписи", STRING),
    FIRST_SIGN_TOKEN("1-я подпись Данные Токена", STRING),
    FIRST_SIGN_TYPE("1-я подпись Тип подписи", STRING),
    FIRST_SIGN_IMSI("1-я подпись IMSI", STRING),
    FIRST_SIGN_CERT_ID("1-я подпись Идентификатор сертификата", STRING),
    FIRST_SIGN_PHONE("1-я подпись Номер телефона", STRING),
    FIRST_SIGN_EMAIL("1-я подпись Адрес электронной почты", STRING),
    FIRST_SIGN_SOURCE("1-я подпись Канал", STRING),
    SENDER_IP_ADDRESS("Отправивший IP адрес", STRING),
    SENDER_LOGIN("Отправивший Логин", STRING),
    SENDER_PHONE("Отправивший Номер телефона", STRING),
    SENDER_EMAIL("Отправивший Адрес электронной почты", STRING),
    SENDER_SOURCE("Отправивший Канал", STRING),
    PRIVATE_IP_ADDRESS("Локальный IP адрес", STRING),
    EPK_ID("ЕПК.ID", STRING),
    DIGITAL_ID("digitalId", STRING),
    SBBOL_GUID("UUID текущей организации пользователя в CББОЛ", STRING),
    REESTR_ID("Id электронного реестра", STRING),
    REESTR_ROW_COUNT("Количество записей в электронном реестре", STRING),
    REESTR_ROW_NUMBER("Номер записи в электронном реестре", STRING);

    private String attributeName;

    private DataType attributeType;

    ClientDefinedAttributeType(String attributeName, DataType attributeType) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeType() {
        return attributeType.name();
    }

    enum DataType {
        STRING, DATE
    }
}
