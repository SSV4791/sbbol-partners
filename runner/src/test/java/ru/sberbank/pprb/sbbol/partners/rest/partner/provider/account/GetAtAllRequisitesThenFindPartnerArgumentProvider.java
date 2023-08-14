package ru.sberbank.pprb.sbbol.partners.rest.partner.provider.account;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class GetAtAllRequisitesThenFindPartnerArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(null, StringUtils.EMPTY),
            Arguments.of(StringUtils.EMPTY, null),
            Arguments.of(null, null),
            Arguments.of(StringUtils.EMPTY, StringUtils.EMPTY)
        );
    }
}
