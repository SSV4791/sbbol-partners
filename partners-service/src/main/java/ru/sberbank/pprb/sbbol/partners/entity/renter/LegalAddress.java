package ru.sberbank.pprb.sbbol.partners.entity.renter;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Адрес регистрации
 */
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_LEGALADDRESS")
@DynamicUpdate
@DynamicInsert
@Entity(name = "LegalAddress")
public class LegalAddress extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RENTER_ID", nullable = false)
    /**
     * Партнер
     */
    private Renter renter;

    /**
     * Индекс
     */
    @Column(length = 6)
    private String zipCode;
    /**
     * Код региона
     */
    @Column(length = 10)
    private String regionCode;
    /**
     * Район
     */
    @Column(length = 50)
    private String region;
    /**
     * Город
     */
    @Column(length = 300)
    private String city;
    /**
     * Населенный пункт
     */
    @Column(length = 300)
    private String locality;
    /**
     * Улица
     */
    @Column(length = 300)
    private String street;
    /**
     * Дом
     */
    @Column(length = 100)
    private String building;
    /**
     * Корпус
     */
    @Column(length = 20)
    private String buildingBlock;
    /**
     * Квартира
     */
    @Column(length = 20)
    private String flat;

    //
    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    // Индекс
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    // Код региона
    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    // Район
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    // Город
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Населенный пункт
    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    // Улица
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    // Дом
    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    // Корпус
    public String getBuildingBlock() {
        return buildingBlock;
    }

    public void setBuildingBlock(String buildingBlock) {
        this.buildingBlock = buildingBlock;
    }

    // Квартира
    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LegalAddress that = (LegalAddress) obj;
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
        return this.getRenter().getUuid();
    }
}
