package ru.sberbank.pprb.sbbol.partners.exception;

/**
 * Ошибка, клиент не является мигрированным
 */
public class PartnerMigrationException extends RuntimeException {

    public PartnerMigrationException() {
        super("Клиент не мигрирован из legacy! Отсутствует AppObj: UfsCounterpartiesDictionaryMigratedToPartners");
    }
}
