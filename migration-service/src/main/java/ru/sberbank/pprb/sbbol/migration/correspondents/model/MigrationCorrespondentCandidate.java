package ru.sberbank.pprb.sbbol.migration.correspondents.model;

import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;

import java.io.Serializable;

/**
 * Контрагент-кандидат для миграции
 */
public class MigrationCorrespondentCandidate implements Serializable {
    /**
     * Наименование
     */
    private String name;
    /**
     * ИНН
     */
    private String inn;
    /**
     * КПП
     */
    private String kpp;
    /**
     * Номер счёта
     */
    private String account;
    /**
     * БИК
     */
    private String bic;
    /**
     * Описание. Комментарий
     */
    private String description;
    /**
     * Номер телефона
     */
    private String corrPhoneNumber;
    /**
     * Адрес электронной почты
     */
    private String corrEmail;
    /**
     * Идентификатор в ППРБ
     */
    private String pprbGuid;
    /**
     * Номер счёта банка
     */
    private String bankAccount;
    /**
     * Идентификатор репликации в Legacy СББОЛ
     */
    private String replicationGuid;
    /**
     * Версия
     */
    private long version;
    /**
     * Признак подписи контрагента
     */
    private boolean signed;
    /**
     * Тип правовой формы
     */
    private MigrationLegalType legalType;

    /**
     * Наименование банка
     */
    private String bankName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
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

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCorrPhoneNumber() {
        return corrPhoneNumber;
    }

    public void setCorrPhoneNumber(String corrPhoneNumber) {
        this.corrPhoneNumber = corrPhoneNumber;
    }

    public String getCorrEmail() {
        return corrEmail;
    }

    public void setCorrEmail(String corrEmail) {
        this.corrEmail = corrEmail;
    }

    public String getPprbGuid() {
        return pprbGuid;
    }

    public void setPprbGuid(String pprbGuid) {
        this.pprbGuid = pprbGuid;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getReplicationGuid() {
        return replicationGuid;
    }

    public void setReplicationGuid(String replicationGuid) {
        this.replicationGuid = replicationGuid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public MigrationLegalType getLegalType() {
        return legalType;
    }

    public void setLegalType(MigrationLegalType legalType) {
        this.legalType = legalType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
