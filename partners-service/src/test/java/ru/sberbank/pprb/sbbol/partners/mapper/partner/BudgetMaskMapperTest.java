package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskForm;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetMaskMapperTest extends BaseConfiguration {

    private static final BudgetMaskMapper mapper = Mappers.getMapper(BudgetMaskMapper.class);

    @Test
    void toBudgetMask() {
        BudgetMask expected = factory.manufacturePojo(BudgetMask.class);
        BudgetMaskEntity actual = mapper.toBudgetMask(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toBudgetMask(actual));
    }

    @Test
    void toBudgetMaskType() {
        BudgetMaskForm typeEnum = factory.manufacturePojo(BudgetMaskForm.class);
        BudgetMaskType addressType = BudgetMaskMapper.toBudgetMaskType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(BudgetMaskMapper.toBudgetMaskType(addressType));
    }
}
