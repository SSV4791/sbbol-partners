package ru.sberbank.pprb.sbbol.partners.rest.partner.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;

import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class PartnerFilterLegalFormArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        String digitalId = randomAlphabetic(10);
        return Stream.of(
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .addLegalFormsItem(LegalForm.PHYSICAL_PERSON)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(1)
                    )
            ),
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .addLegalFormsItem(LegalForm.ENTREPRENEUR)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(1)
                    )
            ),
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .addLegalFormsItem(LegalForm.LEGAL_ENTITY)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(1)
                    )
            ),
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .addLegalFormsItem(LegalForm.LEGAL_ENTITY)
                    .addLegalFormsItem(LegalForm.PHYSICAL_PERSON)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(2)
                    )
            ),
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .addLegalFormsItem(LegalForm.LEGAL_ENTITY)
                    .addLegalFormsItem(LegalForm.ENTREPRENEUR)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(2)
                    )
            ),
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .addLegalFormsItem(LegalForm.ENTREPRENEUR)
                    .addLegalFormsItem(LegalForm.PHYSICAL_PERSON)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(2)
                    )
            )
        );
    }
}
