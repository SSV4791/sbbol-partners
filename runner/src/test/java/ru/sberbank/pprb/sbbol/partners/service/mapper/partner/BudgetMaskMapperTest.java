package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BudgetMaskMapper;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskForm;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetMaskMapperTest extends BaseUnitConfiguration {

    private static final BudgetMaskMapper mapper = Mappers.getMapper(BudgetMaskMapper.class);

    @Test
    @AllureId("34096")
    void toBudgetMask() {
        BudgetMask expected = factory.manufacturePojo(BudgetMask.class);
        BudgetMaskEntity actual = mapper.toBudgetMask(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toBudgetMask(actual));
    }

    @Test
    @AllureId("34096")
    void toBudgetMaskType() {
        BudgetMaskForm typeEnum = factory.manufacturePojo(BudgetMaskForm.class);
        BudgetMaskType addressType = BudgetMaskMapper.toBudgetMaskType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(BudgetMaskMapper.toBudgetMaskType(addressType));
    }
}
