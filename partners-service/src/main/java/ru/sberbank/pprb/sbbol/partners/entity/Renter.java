package ru.sberbank.pprb.sbbol.partners.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Партнер
 */
@Table(name = "T_RENTER")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
@DynamicInsert
@Entity(name = "Renter")
public class Renter extends BaseEntity {
    /**
     * Используется как уникальный идентификатор записи
     */
    @Column(nullable = false, unique = true, length = 36)
    private String uuid;
    /**
     * Идентификатор договора ДБО
     */
    @Column(nullable = false)
    private String digitalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "RENTERTYPE")
    /**
     * Тип сущности Партнер
     */
    private RenterType renterType;

    /**
     * Наименование ЮЛ/ИП
     */
    @Column(length = 350)
    private String legalName;
    /**
     * ИНН
     */
    @Column(length = 12)
    private String inn;
    /**
     * КПП
     */
    @Column(length = 9)
    private String kpp;
    /**
     * ОГРН
     */
    @Column(length = 15)
    private String ogrn;
    /**
     * ОКПО
     */
    @Column(length = 30)
    private String okpo;
    /**
     * Фамилия
     */
    @Column(length = 50)
    private String lastName;
    /**
     * Имя
     */
    @Column(length = 50)
    private String firstName;
    /**
     * Отчество
     */
    @Column(length = 50)
    private String middleName;
    /**
     * Тип удостоверяющего документа (физ. лицо)
     */
    @Enumerated(EnumType.STRING)
    private DulType dulType;
    /**
     * Серия ДУ
     */
    @Column(length = 50)
    private String dulSerie;
    /**
     * Номер ДУЛ
     */
    @Column(length = 50)
    private String dulNumber;
    /**
     * Кем выдан ДУЛ
     */
    @Column(length = 250)
    private String dulDivisionIssue;
    /**
     * Дата выдачи ДУЛ
     */
    @Column(length = 10)
    private LocalDate dulDateIssue;
    /**
     * Код подразделения из ДУЛ
     */
    @Column(length = 50)
    private String dulDivisionCode;
    /**
     * Счет
     */
    @Column(length = 20)
    private String account;
    /**
     * БИК банка
     */
    @Column(length = 9)
    private String bankBic;
    /**
     * Наименование банка
     */
    @Column(length = 160)
    private String bankName;
    /**
     * К/с банка
     */
    @Column(length = 20)
    private String bankAccount;
    /**
     * Телефоны
     */
    @Column(length = 100)
    private String phoneNumbers;
    /**
     * E-mail
     */
    @Column(length = 320)
    private String emails;

    /**
     * Адрес регистрации
     */
    @OneToOne(mappedBy = "renter", cascade = CascadeType.ALL, orphanRemoval = true)
    private LegalAddress legalAddress;

    /**
     * Фактический адрес
     */
    @OneToOne(mappedBy = "renter", cascade = CascadeType.ALL, orphanRemoval = true)
    private PhysicalAddress physicalAddress;

    @Column(
        insertable = false,
        updatable = false,
        nullable = false,
        length = 254
    )
    private String type;

    // Используется как уникальный идентификатор записи
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    // Идентификатор договора ДБО
    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    // Тип
    public RenterType getRenterType() {
        return renterType;
    }

    public void setRenterType(RenterType renterType) {
        this.renterType = renterType;
    }

    // Наименование ЮЛ/ИП
    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    // ИНН
    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    // КПП
    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    // ОГРН
    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    // ОКПО
    public String getOkpo() {
        return okpo;
    }

    public void setOkpo(String okpo) {
        this.okpo = okpo;
    }

    // Фамилия
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Имя
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Отчество
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    // Тип удостоверяющего документа (физ. лицо)
    public DulType getDulType() {
        return dulType;
    }

    public void setDulType(DulType dulType) {
        this.dulType = dulType;
    }

    // Серия ДУ
    public String getDulSerie() {
        return dulSerie;
    }

    public void setDulSerie(String dulSerie) {
        this.dulSerie = dulSerie;
    }

    // Номер ДУЛ
    public String getDulNumber() {
        return dulNumber;
    }

    public void setDulNumber(String dulNumber) {
        this.dulNumber = dulNumber;
    }

    // Кем выдан ДУЛ
    public String getDulDivisionIssue() {
        return dulDivisionIssue;
    }

    public void setDulDivisionIssue(String dulDivisionIssue) {
        this.dulDivisionIssue = dulDivisionIssue;
    }

    // Дата выдачи ДУЛ
    public LocalDate getDulDateIssue() {
        return dulDateIssue;
    }

    public void setDulDateIssue(LocalDate dulDateIssue) {
        this.dulDateIssue = dulDateIssue;
    }

    // Код подразделения из ДУЛ
    public String getDulDivisionCode() {
        return dulDivisionCode;
    }

    public void setDulDivisionCode(String dulDivisionCode) {
        this.dulDivisionCode = dulDivisionCode;
    }

    // Счет
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    // БИК банка
    public String getBankBic() {
        return bankBic;
    }

    public void setBankBic(String bankBic) {
        this.bankBic = bankBic;
    }

    // Наименование банка
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    // К/с банка
    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    // Телефоны
    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    // E-mail
    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    // Адрес регистрации
    public LegalAddress getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(LegalAddress legalAddress) {
        this.legalAddress = legalAddress;
    }

    // Фактический адрес
    public PhysicalAddress getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(PhysicalAddress physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Renter that = (Renter) obj;
        if (getId() == null || that.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : Objects.hash(getId());
    }

    @Override
    public String getHashKey() {
        return getUuid();
    }
}
