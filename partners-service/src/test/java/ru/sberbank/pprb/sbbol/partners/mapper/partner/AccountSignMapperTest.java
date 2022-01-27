package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;

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
        assertThat(expected.getSignCollectionId())
            .isEqualTo(actual.getSignId());
    }
}
