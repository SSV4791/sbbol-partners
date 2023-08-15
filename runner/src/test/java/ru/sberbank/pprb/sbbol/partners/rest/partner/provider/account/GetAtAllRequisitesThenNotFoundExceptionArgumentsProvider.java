package ru.sberbank.pprb.sbbol.partners.rest.partner.provider.account;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;
import ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest;

import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountControllerTest.createValidAccount;

public class GetAtAllRequisitesThenNotFoundExceptionArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        var partner = step("Создание партнера", PartnerControllerTest::createValidPartner);
        var account = step("Создание счета", () -> createValidAccount(partner.getId(), partner.getDigitalId()));
        return Stream.of(
            Arguments.of(
                new AccountAndPartnerRequest()
                    .digitalId(partner.getDigitalId())
                    .account(account.getAccount())
                    .bic(account.getBank().getBic())
                    .bankAccount(account.getBank().getBankAccount().getBankAccount())
                    .inn(partner.getInn())
                    .kpp(null)
                    .name(partner.getOrgName())),
            Arguments.of(
                new AccountAndPartnerRequest()
                    .digitalId(partner.getDigitalId())
                    .account(account.getAccount())
                    .bic(account.getBank().getBic())
                    .bankAccount(account.getBank().getBankAccount().getBankAccount())
                    .inn(null)
                    .kpp(null)
                    .name(partner.getOrgName()))
        );
    }
}
