package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapperImpl;

import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = IdsHistoryMapperImpl.class
)
public class IdsHistoryMapperTest extends BaseUnitConfiguration {

    @Autowired
    private IdsHistoryMapper mapper;

    @Test
    void accountToIdsHistoryEntityTest() {
        var externalId = step("Подготовка тестовых данных. Создание externalId", () -> UUID.randomUUID().toString());
        var accountEntity = step("Подготовка тестовых данных. Создание AccountEntity", () -> factory.manufacturePojo(AccountEntity.class));
        var expected = step("Подготовка тестовых данных. Создание IdsHistoryEntity", () -> {
            var idsHistoryEntity = new IdsHistoryEntity();
            idsHistoryEntity.setExternalId(UUID.fromString(externalId));
            idsHistoryEntity.setPprbEntityId(accountEntity.getUuid());
            idsHistoryEntity.setDigitalId(accountEntity.getDigitalId());
            return idsHistoryEntity;
        });
        var actual = step("Выполнение маппинга", () -> mapper.toIdsHistoryEntity(externalId, accountEntity));

        step("Проверка результата", () ->
            assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("uuid", "lastModifiedDate")
                .isEqualTo(expected)
        );
    }

    @Test
    void partnerToIdsHistoryEntityTest() {
        var externalId = step("Подготовка тестовых данных. Создание externalId", () -> UUID.randomUUID().toString());
        var partnerEntity = step("Подготовка тестовых данных. Создание PartnerEntity", () -> factory.manufacturePojo(PartnerEntity.class));
        var expected = step("Подготовка тестовых данных. Создание IdsHistoryEntity", () -> {
            var idsHistoryEntity = new IdsHistoryEntity();
            idsHistoryEntity.setExternalId(UUID.fromString(externalId));
            idsHistoryEntity.setPprbEntityId(partnerEntity.getUuid());
            idsHistoryEntity.setDigitalId(partnerEntity.getDigitalId());
            return idsHistoryEntity;
        });
        var actual = step("Выполнение маппинга", () -> mapper.toIdsHistoryEntity(externalId, partnerEntity));

        step("Проверка результата", () ->
            assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("uuid", "lastModifiedDate")
                .isEqualTo(expected)
        );
    }
}
