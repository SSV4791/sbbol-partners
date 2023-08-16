package ru.sberbank.pprb.sbbol.partners.partners.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UuidListQueryParamConverter implements Converter<String, List<UUID>> {

    @Override
    public List<UUID> convert(String source) {
        if (StringUtils.hasText(source)) {
            var str = source.replaceAll("[ \\[\\]]", "");
            var ids = str.split(",");
            return Arrays.stream(ids).map(UUID::fromString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
