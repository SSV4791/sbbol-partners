package ru.sberbank.pprb.sbbol.partners.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.service.legalform.PartnerNameResolver;
import ru.sberbank.pprb.sbbol.partners.service.legalform.PartnerNameResolver.PartnerName;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = PartnerNameResolver.class
)
public class PartnerNameResolverTest extends BaseUnitConfiguration {

    @Autowired
    private PartnerNameResolver partnerNameResolver;

    @ParameterizedTest
    @MethodSource("nameWithPartnerName")
    @DisplayName("Определение ФИО для ФЛ")
    void getPartnerName(String name, PartnerName expectedPartnerName) {
        var partnerName = partnerNameResolver.getPartnerName(name);
        assertThat(partnerName)
            .isEqualTo(expectedPartnerName);
    }

    static Stream<? extends Arguments> nameWithPartnerName() {
        return Stream.of(
            Arguments.of(
                "Иванов Иван Иванович",
                new PartnerName("Иванов", "Иван", "Иванович")),
            Arguments.of(
                "Иванов1 Иван Иванович",
                new PartnerName(null, "Иванов1 Иван Иванович", null)),
            Arguments.of(
                "Иванов Иван Иванович улу",
                new PartnerName("Иванов", "Иван", "Иванович улу")),
            Arguments.of(
                "Иванов Иван Иванович угу оглы",
                new PartnerName("Иванов", "Иван", "Иванович угу оглы")),
            Arguments.of(
                "Иванов Иван Иванович угу оглы шесть",
                new PartnerName(null, "Иванов Иван Иванович угу оглы шесть", null)),
            Arguments.of(
                "Иванова(Петрова) Анна-Мария Петровна",
                new PartnerName("Иванова(Петрова)", "Анна-Мария", "Петровна")),
            Arguments.of(
                "Иванова(Петрова) Анна-Мария Махмуд кызы",
                new PartnerName("Иванова(Петрова)", "Анна-Мария", "Махмуд кызы")),
            Arguments.of(
                "Иванова Анна",
                new PartnerName("Иванова", "Анна", null)),
            Arguments.of(
                "Иванова1 Анна",
                new PartnerName(null, "Иванова1 Анна", null)),
            Arguments.of(
                "Иванова(Петрова) Анна-Мария",
                new PartnerName("Иванова(Петрова)", "Анна-Мария", null)),
            Arguments.of(
                "Иван1",
                new PartnerName(null, "Иван1", null)),
            Arguments.of(
                "123",
                new PartnerName(null, "123", null))
        );
    }
}
