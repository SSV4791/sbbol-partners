package ru.sberbank.pprb.sbbol.partners.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormResolver;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

@ContextConfiguration(
    classes = LegalFormResolver.class
)
public class LegalFormResolverTest extends BaseUnitConfiguration {

    @Autowired
    private LegalFormResolver legalFormResolver;

    @ParameterizedTest
    @MethodSource("innWithLegalForm")
    @DisplayName("Определение правового статуса по ИНН")
    void testGetLegalFormFromInn(String inn, LegalForm expectedLegalForm) {
        var legalForm = legalFormResolver.getLegalFormFromInn(inn);
        assertThat(legalForm)
            .isEqualTo(expectedLegalForm);
    }

    @ParameterizedTest
    @MethodSource("innAndAccountsWithLegalForm")
    @DisplayName("Определение правового статуса по счетам и ИНН")
    void testGetLegalFormFromInnAndAccount(String inn, Set<String> accounts, LegalForm expectedLegalForm) {
        var legalForm = legalFormResolver.getLegalFormFromInnAndAccount(inn, accounts);
        assertThat(legalForm)
            .isEqualTo(expectedLegalForm);
    }

    static Stream<? extends Arguments> innWithLegalForm() {
        return Stream.of(
            Arguments.of(
                "1234", LEGAL_ENTITY),
            Arguments.of(
                null, LEGAL_ENTITY),
            Arguments.of(
                "", LEGAL_ENTITY),
            Arguments.of(
                "123456789012", PHYSICAL_PERSON)
        );
    }

    static Stream<? extends Arguments> innAndAccountsWithLegalForm() {
        var nullSet = new HashSet<>();
        nullSet.add(null);

        return Stream.of(
            Arguments.of(
                "0123456789", Set.of("01234567890123456789"), LEGAL_ENTITY),
            Arguments.of(
                "012345678912", Set.of("01234567890123456789"), PHYSICAL_PERSON),
            Arguments.of(
                "123456789012", null, PHYSICAL_PERSON),
            Arguments.of(
                "123456789012", Set.of("text"), PHYSICAL_PERSON),
            Arguments.of(
                "012345678912", Set.of("454--всего20символов"), ENTREPRENEUR),
            Arguments.of(
                "012345678912", Set.of("42111всего20символов"), ENTREPRENEUR),
            Arguments.of(
                null, null, LEGAL_ENTITY),
            Arguments.of(
                "123", null, LEGAL_ENTITY),
            Arguments.of(
                null, new HashSet<String>(), LEGAL_ENTITY),
            Arguments.of(
                "123", Set.of(""), LEGAL_ENTITY),
            Arguments.of(
                "123", nullSet, LEGAL_ENTITY),
            Arguments.of(
                "123", Set.of("40803всего20символов"), PHYSICAL_PERSON),
            Arguments.of(
                "123", Set.of("423--всего20символов"), PHYSICAL_PERSON),
            Arguments.of(
                "123", Set.of("40802всего20символов"), ENTREPRENEUR),
            Arguments.of(
                "123", Set.of("454--всего20символов"), ENTREPRENEUR),
            Arguments.of(
                "123", Set.of("XXXXXвсего20символов"), LEGAL_ENTITY),
            Arguments.of(
                "123", Set.of("40820тут18символов"), LEGAL_ENTITY)
        );
    }
}
