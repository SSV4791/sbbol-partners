package ru.sberbank.pprb.sbbol.partners.model.kafka;

import java.time.LocalDateTime;

public class RenterMigrationEvent extends BasePartnerEvent {

    private String partnerId;

    private String renterId;

    private LocalDateTime created;

    public RenterMigrationEvent() {}

    public RenterMigrationEvent(String digitalId, String partnerId, String renterId, LocalDateTime created) {
        super(digitalId);
        this.partnerId = partnerId;
        this.renterId = renterId;
        this.created = created;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "RenterMigrationEvent{" +
            "digitalId='" + getDigitalId() + '\'' +
            "partnerId='" + partnerId + '\'' +
            ", renterId='" + renterId + '\'' +
            ", created=" + created +
            '}';
    }
}
