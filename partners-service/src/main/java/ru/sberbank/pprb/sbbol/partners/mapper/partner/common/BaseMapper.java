package ru.sberbank.pprb.sbbol.partners.mapper.partner.common;

import org.springframework.util.StringUtils;

import java.util.UUID;

public interface BaseMapper {

    default UUID mapUuid(final String id) {
        if (StringUtils.hasText(id)) {
            var value = id.replaceAll("[\\[\\]]", "");
            return UUID.fromString(value);
        }
        return null;
    }

    default String mapUuid(final UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
}
