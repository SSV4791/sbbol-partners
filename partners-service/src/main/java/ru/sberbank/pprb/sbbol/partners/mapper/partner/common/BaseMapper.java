package ru.sberbank.pprb.sbbol.partners.mapper.partner.common;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Mapper
public interface BaseMapper {

    @Named("mapUuid")
    static UUID mapUuid(final String id) {
        if (StringUtils.hasText(id)) {
            var value = id.replaceAll("[\\[\\]]", "");
            return UUID.fromString(value);
        }
        return null;
    }

    @Named("mapUuid")
    static String mapUuid(final UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @NotNull
    static String saveSearchString(String... search) {
        return Stream.of(
                search
            )
            .filter(Objects::nonNull)
            .map(it -> it.replace(SPACE, EMPTY))
            .collect(Collectors.joining(EMPTY))
            .toLowerCase(Locale.ROOT);
    }

    @NotNull
    static String prepareSearchString(String... search) {
        return Stream.of(
                search
            )
            .filter(Objects::nonNull)
            .filter(it -> !EMPTY.equals(it))
            .collect(Collectors.joining(SPACE));
    }
}
