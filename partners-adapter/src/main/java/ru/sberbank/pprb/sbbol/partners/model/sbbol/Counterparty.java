package ru.sberbank.pprb.sbbol.partners.model.sbbol;

import java.io.Serializable;

/**
 * Контрагент
 */
public class Counterparty implements Serializable {

    /**
     * GUID операции в справочнике контрагентов
     */
    private String operationGuid;

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
     * Номер счета контрагента"
     */
    private String account;

    /**
     * Бик Банка
     */
    private String bankBic;

    /**
     * Номер корр.счета контрагента
     */
    private String corrAccount;

    /**
     * Наименование банка
     */
    private String bankName;

    /**
     * Город банка
     */
    private String bankCity;

    /**
     * Тип населённого пункта банка
     */
    private String settlementType;

    /**
     * GUID контрагента в ППРБ
     */
    private String pprbGuid;

    /**
     * Признак 'подписан'
     */
    private Boolean signed;

    /**
     * Номер мобильного телефона контрагента
     */
    private String counterpartyPhone;

    /**
     * Адрес электронной почты контрагента
     */
    private String counterpartyEmail;

    /**
     * Комментарий пользователя
     */
    private String description;

    public String getOperationGuid() {
        return operationGuid;
    }

    public void setOperationGuid(String operationGuid) {
        this.operationGuid = operationGuid;
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

    public String getCorrAccount() {
        return corrAccount;
    }

    public void setCorrAccount(String corrAccount) {
        this.corrAccount = corrAccount;
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

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
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

    public String getCounterpartyPhone() {
        return counterpartyPhone;
    }

    public void setCounterpartyPhone(String counterpartyPhone) {
        this.counterpartyPhone = counterpartyPhone;
    }

    public String getCounterpartyEmail() {
        return counterpartyEmail;
    }

    public void setCounterpartyEmail(String counterpartyEmail) {
        this.counterpartyEmail = counterpartyEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Counterparty{" +
            "operationGuid='" + operationGuid + '\'' +
            ", name='" + name + '\'' +
            ", taxNumber='" + taxNumber + '\'' +
            ", kpp='" + kpp + '\'' +
            ", account='" + account + '\'' +
            ", bankBic='" + bankBic + '\'' +
            ", corrAccount='" + corrAccount + '\'' +
            ", bankName='" + bankName + '\'' +
            ", bankCity='" + bankCity + '\'' +
            ", settlementType='" + settlementType + '\'' +
            ", pprbGuid='" + pprbGuid + '\'' +
            ", signed=" + signed +
            ", counterpartyPhone='" + counterpartyPhone + '\'' +
            ", counterpartyEmail='" + counterpartyEmail + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
