package ru.sberbank.pprb.sbbol.partners.mapper.partner.common;

import java.util.UUID;

public interface BaseMapper {

    default UUID mapUuid(final String id) {
        return id != null ? UUID.fromString(id) : null;
    }

}
