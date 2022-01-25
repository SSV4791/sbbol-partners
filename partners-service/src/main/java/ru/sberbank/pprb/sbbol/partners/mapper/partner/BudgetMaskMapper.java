package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskForm;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BudgetMaskMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(budgetMask.getUuid().toString())")
    @Mapping(target = "maskType", source = "type", qualifiedByName = "toBudgetMaskType")
    BudgetMask toBudgetMask(BudgetMaskEntity budgetMask);

    @Named("toBudgetMaskType")
    static BudgetMaskForm toBudgetMaskType(BudgetMaskType budgetMaskType) {
        return budgetMaskType != null ? BudgetMaskForm.valueOf(budgetMaskType.name()) : null;
    }

    @Mapping(target = "uuid", expression = "java(mapUuid(budgetMask.getId()))")
    @Mapping(target = "type", source = "maskType", qualifiedByName = "toBudgetMaskType")
    BudgetMaskEntity toBudgetMask(BudgetMask budgetMask);

    @Named("toBudgetMaskType")
    static BudgetMaskType toBudgetMaskType(BudgetMaskForm budgetMaskType) {
        return budgetMaskType != null ? BudgetMaskType.valueOf(budgetMaskType.getValue()) : null;
    }
}
