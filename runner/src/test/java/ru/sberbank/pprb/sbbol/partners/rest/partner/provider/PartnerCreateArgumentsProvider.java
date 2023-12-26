package ru.sberbank.pprb.sbbol.partners.rest.partner.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidPartner;

public class PartnerCreateArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(
                getValidPartner(randomAlphabetic(10), LegalForm.LEGAL_ENTITY)
            ),
            Arguments.of(
                getValidPartner(randomAlphabetic(10), LegalForm.LEGAL_ENTITY)
                    .firstName(null)
                    .secondName(null)
                    .middleName(null)
                    .kpp(null)
                    .ogrn(null)
                    .okpo(null)
                    .phones(null)
                    .emails(null)
                    .comment(null)
            ),
            Arguments.of(
                getValidPartner(randomAlphabetic(10), LegalForm.LEGAL_ENTITY)
                    .kpp("1ww^ц{ъ9p")
            ),
            Arguments.of(
                getValidPartner(randomAlphabetic(10), LegalForm.LEGAL_ENTITY)
                    .kpp("0")
            ),
            Arguments.of(
                getValidPartner(randomAlphabetic(10), LegalForm.ENTREPRENEUR)
                    .firstName(null)
                    .secondName(null)
                    .middleName(null)
                    .kpp(null)
                    .ogrn(null)
                    .okpo(null)
                    .phones(null)
                    .emails(null)
                    .comment(null)
            ),
            Arguments.of(
                getValidPartner(randomAlphabetic(10), LegalForm.PHYSICAL_PERSON)
                    .orgName(null)
                    .firstName(randomAlphabetic(10))
                    .secondName(randomAlphabetic(10))
                    .middleName(null)
                    .inn(null)
                    .kpp(null)
                    .ogrn(null)
                    .okpo(null)
                    .phones(null)
                    .emails(null)
                    .comment(null)
            )
        );
    }
}
