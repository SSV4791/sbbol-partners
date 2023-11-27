package ru.sberbank.pprb.sbbol.partners.repository.partner.dto;

import java.util.Objects;
import java.util.UUID;

public class UuidDto {

    private UUID uuid;

    public UuidDto(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UuidDto uuidDto = (UuidDto) o;
        return Objects.equals(uuid, uuidDto.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
