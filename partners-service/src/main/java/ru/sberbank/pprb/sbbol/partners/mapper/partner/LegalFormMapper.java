package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeLegalFormEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

@Loggable
@Mapper
public interface LegalFormMapper extends BaseMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    DocumentTypeLegalFormEntity toDocumentTypeLegalFormEntity(LegalForm legalForm);

    default LegalForm toLegalForm(DocumentTypeLegalFormEntity documentTypeLegalFormEntity) {
        return LegalForm.valueOf(documentTypeLegalFormEntity.getLegalForm());
    }
}
