package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSignMapperTest extends BaseConfiguration {

    private static final AccountSingMapper mapper = Mappers.getMapper(AccountSingMapper.class);

    @Test
    void testToAccount() {
        var expected = factory.manufacturePojo(AccountEntity.class);
        var actual = mapper.toSignAccount(expected);

        assertThat(expected.getUuid())
            .isEqualTo(UUID.fromString(actual.getAccountId()));
        assertThat(expected.getState().name())
            .isEqualTo(actual.getState().name());
    }

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
        assertThat(expected.getAccountUuid().toString())
            .isEqualTo(actual.getAccountId());
        assertThat(expected.getEntityUuid().toString())
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
        assertThat(expected.getAccountUuid().toString())
            .isEqualTo(detail.getAccountId());
        assertThat(expected.getEntityUuid().toString())
            .isEqualTo(detail.getEntityId());
    }

    @Test
    void testToSign() {
        var expected = factory.manufacturePojo(AccountSignDetail.class);
        var actual = mapper.toSing(expected, UUID.randomUUID());

        assertThat(expected.getEntityId())
            .isEqualTo(actual.getEntityUuid().toString());
        assertThat(expected.getDigest())
            .isEqualTo(actual.getDigest());
        assertThat(expected.getSign())
            .isEqualTo(actual.getSign());
        assertThat(expected.getAccountId())
            .isEqualTo(actual.getAccountUuid().toString());
        assertThat(expected.getExternalDataSignFileId())
            .isEqualTo(actual.getExternalDataSignFileId());
        assertThat(expected.getExternalDataFileId())
            .isEqualTo(actual.getExternalDataFileId());
        assertThat(expected.getDateTimeOfSign())
            .isEqualTo(actual.getDateTimeOfSign());
    }
}
