package ru.sberbank.pprb.sbbol.partners.service.mapper.renter;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterPartnerMapper;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RenterPartnerMapperTest extends BaseUnitConfiguration {

    private static final RenterPartnerMapper mapper = Mappers.getMapper(RenterPartnerMapper.class);

    @Test
    @AllureId("34055")
    void toPartner() {
        Renter expected = factory.manufacturePojo(Renter.class);
        var renter = mapper.toPartner(expected);
        Renter actual = mapper.toRenter(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "dulType",
                "dulName",
                "dulSerie",
                "dulNumber",
                "dulDivisionIssue",
                "dulDateIssue",
                "dulDivisionCode",
                "account",
                "bankBic",
                "bankName",
                "bankAccount",
                "legalAddress",
                "physicalAddress",
                "checkResults"
            )
            .isEqualTo(actual);
    }

    @Test
    @AllureId("34106")
    void toPhones() {
        String phone = RandomStringUtils.randomAlphabetic(10);
        String digitalId = RandomStringUtils.randomAlphabetic(10);
        var phones = mapper.toPhones(phone, digitalId);
        var actual = RenterPartnerMapper.toRenterPhone(phones);
        assertThat(phone)
            .isEqualTo(actual);
    }

    @Test
    @AllureId("34091")
    void toEmails() {
        String email = RandomStringUtils.randomAlphabetic(10);
        String digitalId = RandomStringUtils.randomAlphabetic(10);
        var emails = mapper.toEmails(email, digitalId);
        var actual = RenterPartnerMapper.toRenterEmail(emails);
        assertThat(email)
            .isEqualTo(actual);
    }

    @Test
    @AllureId("34074")
    void toAccount() {
        Renter expected = factory.manufacturePojo(Renter.class);
        var account = mapper.toAccount(expected);
        assertThat(expected.getUuid())
            .isEqualTo(account.getPartnerUuid().toString());
        assertThat(expected.getDigitalId())
            .isEqualTo(account.getDigitalId());
        assertThat(expected.getAccount())
            .isEqualTo(account.getAccount());
        assertThat(account)
            .isEqualTo(account.getBank().getAccount());
        assertThat(expected.getBankName())
            .isEqualTo(account.getBank().getName());
        assertThat(expected.getBankBic())
            .isEqualTo(account.getBank().getBic());
        assertThat(account.getBank())
            .isEqualTo(account.getBank().getBankAccount().getBank());
        assertThat(expected.getBankAccount())
            .isEqualTo(account.getBank().getBankAccount().getAccount());
    }

    @Test
    @AllureId("34086")
    void toBanks() {
        Renter expected = factory.manufacturePojo(Renter.class);
        var bank = RenterPartnerMapper.toBank(expected);
        assertThat(expected.getBankName())
            .isEqualTo(bank.getName());
        assertThat(expected.getBankBic())
            .isEqualTo(bank.getBic());
        assertThat(bank)
            .isEqualTo(bank.getBankAccount().getBank());
        assertThat(expected.getBankAccount())
            .isEqualTo(bank.getBankAccount().getAccount());
    }

    @Test
    @AllureId("34065")
    void toAddress() {
        var partnerUuid = factory.manufacturePojo(UUID.class);
        var digitalId = factory.manufacturePojo(String.class);
        RenterAddress expected = factory.manufacturePojo(RenterAddress.class);
        var renter = mapper.toAddress(expected, partnerUuid, digitalId);
        var actual = mapper.toRenterAddress(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(actual);
    }

    @Test
    @AllureId("34098")
    void toDocument() {
        var partnerUuid = factory.manufacturePojo(UUID.class);
        Renter expected = factory.manufacturePojo(Renter.class);
        var document = mapper.toDocument(expected, partnerUuid);
        assertThat(expected.getDulSerie())
            .isEqualTo(document.getSeries());
        assertThat(expected.getDulNumber())
            .isEqualTo(document.getNumber());
        assertThat(expected.getDulDivisionIssue())
            .isEqualTo(document.getDivisionIssue());
        assertThat(expected.getDulDateIssue())
            .isEqualTo(document.getDateIssue());
        assertThat(expected.getDulDivisionCode())
            .isEqualTo(document.getDivisionCode());
    }
}
