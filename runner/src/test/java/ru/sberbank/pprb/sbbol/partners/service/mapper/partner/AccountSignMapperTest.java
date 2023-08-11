package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;

import java.sql.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountSignMapperTest extends BaseUnitConfiguration {

    private static final AccountSingMapper mapper = Mappers.getMapper(AccountSingMapper.class);

    @Test
    void testToSignAccount() {
        var expected = factory.manufacturePojo(SignEntity.class);
        var actual = mapper.toSignAccount(expected);

        assertThat(expected.getDateTimeOfSign())
            .isEqualTo(actual.getDateTimeOfSign());
        assertThat(expected.getDigest())
            .isEqualTo(actual.getDigest());
        assertThat(expected.getSign())
            .isEqualTo(actual.getSign());
        assertThat(expected.getExternalDataFileId())
            .isEqualTo(actual.getExternalDataFileId());
        assertThat(expected.getExternalDataSignFileId())
            .isEqualTo(actual.getExternalDataSignFileId());
        assertThat(expected.getAccountUuid())
            .isEqualTo(actual.getAccountId());
        assertThat(expected.getEntityUuid())
            .isEqualTo(actual.getEntityId());
    }

    @Test
    void testToSignAccountInfo() {
        var expected = factory.manufacturePojo(SignEntity.class);
        var expectedDigitalId = RandomStringUtils.randomAlphabetic(10);
        var actual = mapper.toSignAccount(expected, expectedDigitalId);
        var detail = actual.getAccountSignDetail();

        assertThat(expectedDigitalId)
            .isEqualTo(actual.getDigitalId());
        assertThat(expected.getDateTimeOfSign())
            .isEqualTo(detail.getDateTimeOfSign());
        assertThat(expected.getDigest())
            .isEqualTo(detail.getDigest());
        assertThat(expected.getSign())
            .isEqualTo(detail.getSign());
        assertThat(expected.getExternalDataFileId())
            .isEqualTo(detail.getExternalDataFileId());
        assertThat(expected.getExternalDataSignFileId())
            .isEqualTo(detail.getExternalDataSignFileId());
        assertThat(expected.getAccountUuid())
            .isEqualTo(detail.getAccountId());
        assertThat(expected.getEntityUuid())
            .isEqualTo(detail.getEntityId());
    }

    @Test
    void testToSign() {
        var partnerUuid = factory.manufacturePojo(UUID.class);
        var digitalId = factory.manufacturePojo(String.class);
        var expected = factory.manufacturePojo(AccountSignDetail.class);
        var actual = mapper.toSing(expected, partnerUuid, digitalId);

        assertThat(expected.getEntityId())
            .isEqualTo(actual.getEntityUuid());
        assertThat(expected.getDigest())
            .isEqualTo(actual.getDigest());
        assertThat(expected.getSign())
            .isEqualTo(actual.getSign());
        assertThat(expected.getAccountId())
            .isEqualTo(actual.getAccountUuid());
        assertThat(expected.getExternalDataSignFileId())
            .isEqualTo(actual.getExternalDataSignFileId());
        assertThat(expected.getExternalDataFileId())
            .isEqualTo(actual.getExternalDataFileId());
        assertThat(expected.getDateTimeOfSign())
            .isEqualTo(actual.getDateTimeOfSign());
    }

    @Test
    void testToCounterpartySignData() {
        var signProfileId = factory.manufacturePojo(Long.class);
        var signEntity = factory.manufacturePojo(SignEntity.class);
        signEntity.setSignProfileId(String.valueOf(signProfileId));
        var actual = mapper.toCounterpartySignData(signEntity);

        var expected = new CounterpartySignData();
        expected.setBase64sign(signEntity.getSign());
        expected.setSignDate(Date.from(signEntity.getDateTimeOfSign().toInstant()));
        expected.setDigest(signEntity.getDigest());
        expected.setSignProfileId(Long.parseLong(signEntity.getSignProfileId()));
        expected.setPprbGuid(signEntity.getAccountUuid());
        expected.setDcsId("default");

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
