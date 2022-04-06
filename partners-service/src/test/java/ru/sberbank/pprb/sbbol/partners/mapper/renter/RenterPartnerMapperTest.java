package ru.sberbank.pprb.sbbol.partners.mapper.renter;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;

import static org.assertj.core.api.Assertions.assertThat;

class RenterPartnerMapperTest extends BaseConfiguration {

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
        RenterAddress expected = factory.manufacturePojo(RenterAddress.class);
        var renter = mapper.toAddress(expected);
        var actual = mapper.toRenterAddress(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(actual);
    }

    @Test
    @AllureId("34098")
    void toDocument() {
        Renter expected = factory.manufacturePojo(Renter.class);
        var document = mapper.toDocument(expected);
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
