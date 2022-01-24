package ru.sberbank.pprb.sbbol.partners.model.sbbol;

import java.io.Serializable;

/**
 * Контрагент
 */
public class CounterpartyView implements Serializable {

    /**
     * GUID контрагента в ППРБ
     */
    private String pprbGuid;

    /**
     * Название Контрагента
     */
    private String name;

    /**
     * ИНН контрагента
     */
    private String taxNumber;

    /**
     * КПП контрагента
     */
    private String kpp;

    /**
     * Номер счета контрагента
     */
    private String account;

    /**
     * Бик Банка
     */
    private String bankBic;

    /**
     * Корр счёт банка
     */
    private String bankAccount;

    /**
     * Наименование банка
     */
    private String bankName;

    /**
     * Населенный пункт банка
     */
    private String bankCity;

    /**
     * Тип населённого пункта банка
     */
    private String bankSettlementType;

    /**
     * Признак 'подписан'
     */
    private boolean signed;

    /**
     * Признак, определяющий является ли контрагент поставщиком ЖКУ
     */
    private Boolean housingServicesProvider;

    /**
     * Комментарий пользователя
     */
    private String description;

    public String getPprbGuid() {
        return pprbGuid;
    }

    public void setPprbGuid(String pprbGuid) {
        this.pprbGuid = pprbGuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBankBic() {
        return bankBic;
    }

    public void setBankBic(String bankBic) {
        this.bankBic = bankBic;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public String getBankSettlementType() {
        return bankSettlementType;
    }

    public void setBankSettlementType(String bankSettlementType) {
        this.bankSettlementType = bankSettlementType;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public Boolean getHousingServicesProvider() {
        return housingServicesProvider;
    }

    public void setHousingServicesProvider(Boolean housingServicesProvider) {
        this.housingServicesProvider = housingServicesProvider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
