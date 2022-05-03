package ru.sberbank.pprb.sbbol.partners.legacy.model;

/**
 * Запрос на поиск контрагента по реквизитам
 */
public class CounterpartyCheckRequisites {

    /**
     * Идентификатор личного кабинета клиента
     */
    private String digitalId;

    /**
     * Имя контрагента
     */
    private String name;

    /**
     * КПП
     */
    private String kpp;

    /**
     * Номер счёта
     */
    private String accountNumber;

    /**
     * ИНН
     */
    private String taxNumber;

    /**
     * БИК банка
     */
    private String bankBic;

    /**
     * Корр. счёт
     */
    private String bankAccount;

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
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

    @Override
    public String toString() {
        return "CounterpartyCheckRequisites{" +
            "digitalId='" + digitalId + '\'' +
            ", name='" + name + '\'' +
            ", kpp='" + kpp + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            ", taxNumber='" + taxNumber + '\'' +
            ", bankBic='" + bankBic + '\'' +
            ", bankAccount='" + bankAccount + '\'' +
            '}';
    }
}
