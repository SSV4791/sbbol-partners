package ru.sberbank.pprb.sbbol.partners.repository.partner.dto;

import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;

import java.util.Objects;

public class PartnerLegalTypeDto {

    private LegalType legalType;

    private PartnerType type;

    public PartnerLegalTypeDto(LegalType legalType, PartnerType type) {
        this.legalType = legalType;
        this.type = type;
    }

    public LegalType getLegalType() {
        return legalType;
    }

    public void setLegalType(LegalType legalType) {
        this.legalType = legalType;
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
        PartnerLegalTypeDto that = (PartnerLegalTypeDto) o;
        return legalType == that.legalType && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(legalType, type);
    }
}
