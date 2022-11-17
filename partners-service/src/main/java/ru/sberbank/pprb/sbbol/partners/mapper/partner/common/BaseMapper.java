package ru.sberbank.pprb.sbbol.partners.mapper.partner.common;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

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

    @NotNull
    default String prepareSearchString(String... search) {
        return Stream.of(
                search
            )
            .filter(Objects::nonNull)
            .map(it -> it.replace(SPACE, EMPTY))
            .collect(Collectors.joining(EMPTY));
    }

    @NotNull
    default String saveSearchString(String... search) {
        return Stream.of(
                search
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(SPACE));
    }
}
