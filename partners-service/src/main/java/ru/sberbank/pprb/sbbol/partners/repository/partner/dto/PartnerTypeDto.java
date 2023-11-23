package ru.sberbank.pprb.sbbol.partners.repository.partner.dto;

import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;

import java.util.Objects;

public class PartnerTypeDto {

    private PartnerType type;

    public PartnerTypeDto(PartnerType type) {
        this.type = type;
    }

    public PartnerType getType() {
        return type;
    }

    public void setType(PartnerType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartnerTypeDto that = (PartnerTypeDto) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}

