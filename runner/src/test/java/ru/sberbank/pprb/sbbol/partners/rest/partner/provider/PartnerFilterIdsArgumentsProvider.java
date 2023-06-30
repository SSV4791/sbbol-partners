package ru.sberbank.pprb.sbbol.partners.rest.partner.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest.post;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.BASE_ROUTE_PATH;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidPartner;

public class PartnerFilterIdsArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        int partnersSize = 5;
        String digitalId = randomAlphabetic(10);
        List<String> ids = new ArrayList<>(partnersSize);
        for (int i = 0; i < partnersSize; i++) {
            Partner partner = post(
                    BASE_ROUTE_PATH,
                HttpStatus.CREATED,
                getValidPartner(digitalId, LegalForm.LEGAL_ENTITY),
                Partner.class
            );
            ids.add(partner.getId());
        }
        return Stream.of(
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .ids(List.of(ids.get(0)))
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(1)
                    )
            ),
            Arguments.of(
                new PartnersFilter()
                    .digitalId(digitalId)
                    .ids(ids)
                    .pagination(
                        new Pagination()
                            .offset(0)
                            .count(5)
                    )
            )
        );
    }
}
