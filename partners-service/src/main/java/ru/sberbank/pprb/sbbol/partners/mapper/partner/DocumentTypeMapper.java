package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;

import java.util.List;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        LegalFormMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface DocumentTypeMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(documentType.getUuid() == null ? null :documentType.getUuid().toString())")
    @Mapping(target = "documentType", source = "systemName")
    DocumentType toDocumentType(DocumentTypeEntity documentType);

    List<DocumentType> toDocumentType(List<DocumentTypeEntity> documentType);

    @Mapping(target = "uuid", expression = "java(mapUuid(documentType.getId()))")
    @Mapping(target = "systemName", source = "documentType")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    DocumentTypeEntity toDocumentType(DocumentType documentType);
}
