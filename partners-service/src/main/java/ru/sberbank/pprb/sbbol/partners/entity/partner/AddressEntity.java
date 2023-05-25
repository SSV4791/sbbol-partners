package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "address",
    indexes = {
        @Index(name = "address_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_address_digital_id", columnList = "digital_id"),
        @Index(name = "i_address_unified_uuid", columnList = "unified_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class AddressEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "unified_uuid", nullable = false)
    private UUID unifiedUuid;

    @Column(name = "digital_id", nullable = false, length = 40)
    private String digitalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private AddressType type;

    @Column(name = "zip_code", length = 6)
    private String zipCode;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column(name = "country_iso_code", nullable = false)
    private String countryIsoCode;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "administration_unit_code")
    private String administrationUnitCode;

    @Column(name = "administration_unit")
    private String administrationUnit;

    @Column(name = "region_code", length = 10)
    private String regionCode;

    @Column(name = "region", length = 50)
    private String region;

    @Column(name = "area", length = 300)
    private String area;

    @Column(name = "city", length = 300)
    private String city;

    @Column(name = "location", length = 300)
    private String location;

    @Column(name = "street", length = 300)
    private String street;

    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "building_block", length = 20)
    private String buildingBlock;

    @Column(name = "flat", length = 20)
    private String flat;

    @Column(name = "full_address")
    private String fullAddress;

    public UUID getUnifiedUuid() {
        return unifiedUuid;
    }

    public void setUnifiedUuid(UUID unifiedUuid) {
        this.unifiedUuid = unifiedUuid;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdministrationUnitCode() {
        return administrationUnitCode;
    }

    public void setAdministrationUnitCode(String administrationUnitCode) {
        this.administrationUnitCode = administrationUnitCode;
    }

    public String getAdministrationUnit() {
        return administrationUnit;
    }

    public void setAdministrationUnit(String administrationUnit) {
        this.administrationUnit = administrationUnit;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBuildingBlock() {
        return buildingBlock;
    }

    public void setBuildingBlock(String buildingBlock) {
        this.buildingBlock = buildingBlock;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Override
    public int hashCode() {
        return getUuid() == null ? super.hashCode() : Objects.hash(getUuid());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AddressEntity that = (AddressEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }
}
