package ru.sberbank.pprb.sbbol.partners.model.kafka;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = RenterMigrationEvent.class, name = "renterMigrationEvent")
})
public class BasePartnerEvent {

    private String digitalId;

    public BasePartnerEvent() {
    }

    public BasePartnerEvent(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasePartnerEvent)) return false;
        BasePartnerEvent that = (BasePartnerEvent) o;
        return Objects.equals(digitalId, that.digitalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digitalId);
    }

    @Override
    public String toString() {
        return "BasePartnerEvent{" +
            "digitalId='" + digitalId + '\'' +
            '}';
    }
}
