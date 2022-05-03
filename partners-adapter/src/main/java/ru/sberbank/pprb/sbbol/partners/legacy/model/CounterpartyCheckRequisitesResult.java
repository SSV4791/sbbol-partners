package ru.sberbank.pprb.sbbol.partners.legacy.model;

/**
 * Результат проверки в СББОЛ реквизитов получателя платежа по справочнику контрагентов
 */
public class CounterpartyCheckRequisitesResult {

    /**
     * Guid контрагента в ППРБ (null - если контрагент не найден)
     */
    private String pprbGuid;

    /**
     * Признак, что контрагент подтвержден
     */
    private Boolean signed;

    public CounterpartyCheckRequisitesResult() {
    }

    public CounterpartyCheckRequisitesResult(String pprbGuid, Boolean signed) {
        this.pprbGuid = pprbGuid;
        this.signed = signed;
    }

    public String getPprbGuid() {
        return pprbGuid;
    }

    public void setPprbGuid(String pprbGuid) {
        this.pprbGuid = pprbGuid;
    }

    public Boolean getSigned() {
        return signed;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
    }
}
