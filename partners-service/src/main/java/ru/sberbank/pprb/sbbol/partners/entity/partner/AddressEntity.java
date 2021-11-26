package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "address", indexes = {
    @Index(name = "i_address_partner_uuid", columnList = "partner_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class AddressEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_uuid", nullable = false)
    private PartnerEntity partner;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 254)
    private AddressType type;

    @Column(name = "zip_code", length = 6)
    private String zipCode;

    @Column(name = "region", length = 50)
    private String region;

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

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getBuildingBlock() {
        return buildingBlock;
    }

    public void setBuildingBlock(String buildingBlock) {
        this.buildingBlock = buildingBlock;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    @Override
    public String getHashKey() {
        return partner.getId().toString();
    }
}
