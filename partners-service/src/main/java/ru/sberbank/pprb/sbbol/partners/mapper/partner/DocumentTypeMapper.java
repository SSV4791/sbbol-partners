package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;

import java.util.List;

@Loggable
@Mapper(
    uses = {
        BaseMapper.class,
        LegalFormMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface DocumentTypeMapper {

    @Mapping(target = "id", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "documentType", source = "systemName")
    DocumentType toDocumentType(DocumentTypeEntity documentType);

    List<DocumentType> toDocumentType(List<DocumentTypeEntity> documentType);

    @Mapping(target = "uuid", source = "id", qualifiedByName = "mapUuid")
    @Mapping(target = "systemName", source = "documentType")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    DocumentTypeEntity toDocumentType(DocumentType documentType);
}
