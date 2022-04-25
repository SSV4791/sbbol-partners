package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeLegalFormEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LegalFormMapper extends BaseMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    DocumentTypeLegalFormEntity toDocumentTypeLegalFormEntity(LegalForm legalForm);

    default LegalForm toLegalForm(DocumentTypeLegalFormEntity documentTypeLegalFormEntity) {
        return LegalForm.valueOf(documentTypeLegalFormEntity.getLegalForm());
    }
}
