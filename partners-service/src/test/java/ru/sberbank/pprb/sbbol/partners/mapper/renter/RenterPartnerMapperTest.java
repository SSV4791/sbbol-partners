package ru.sberbank.pprb.sbbol.partners.mapper.renter;

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
    void toPhones() {
        String phone = RandomStringUtils.randomAlphabetic(10);
        String digitalId = RandomStringUtils.randomAlphabetic(10);
        var phones = mapper.toPhones(phone, digitalId);
        var actual = RenterPartnerMapper.toRenterPhone(phones);
        assertThat(phone)
            .isEqualTo(actual);
    }

    @Test
    void toEmails() {
        String email = RandomStringUtils.randomAlphabetic(10);
        String digitalId = RandomStringUtils.randomAlphabetic(10);
        var emails = mapper.toEmails(email, digitalId);
        var actual = RenterPartnerMapper.toRenterEmail(emails);
        assertThat(email)
            .isEqualTo(actual);
    }

    @Test
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
            .isEqualTo(account.getBanks().get(0).getAccount());
        assertThat(expected.getBankName())
            .isEqualTo(account.getBanks().get(0).getName());
        assertThat(expected.getBankBic())
            .isEqualTo(account.getBanks().get(0).getBic());
        assertThat(account.getBanks().get(0))
            .isEqualTo(account.getBanks().get(0).getBankAccounts().get(0).getBank());
        assertThat(expected.getBankAccount())
            .isEqualTo(account.getBanks().get(0).getBankAccounts().get(0).getAccount());
    }

    @Test
    void toBanks() {
        Renter expected = factory.manufacturePojo(Renter.class);
        var banks = RenterPartnerMapper.toBanks(expected);
        assertThat(expected.getBankName())
            .isEqualTo(banks.get(0).getName());
        assertThat(expected.getBankBic())
            .isEqualTo(banks.get(0).getBic());
        assertThat(banks.get(0))
            .isEqualTo(banks.get(0).getBankAccounts().get(0).getBank());
        assertThat(expected.getBankAccount())
            .isEqualTo(banks.get(0).getBankAccounts().get(0).getAccount());
    }

    @Test
    void toAddress() {
        RenterAddress expected = factory.manufacturePojo(RenterAddress.class);
        var renter = mapper.toAddress(expected);
        var actual = mapper.toRenterAddress(renter);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(actual);
    }

    @Test
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