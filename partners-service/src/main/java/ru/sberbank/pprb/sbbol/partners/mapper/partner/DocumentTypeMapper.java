package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DocumentTypeMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(documentType.getUuid().toString())")
    @Mapping(target = "documentType", source = "systemName")
    DocumentType toDocumentType(DocumentTypeEntity documentType);

    List<DocumentType> toDocumentType(List<DocumentTypeEntity> documentType);

    @Mapping(target = "uuid", expression = "java(mapUuid(documentType.getId()))")
    @Mapping(target = "systemName", source = "documentType")
    DocumentTypeEntity toDocumentType(DocumentType documentType);

    @Named("updateDocument")
    @Mapping(target = "uuid", expression = "java(mapUuid(document.getId()))")
    @Mapping(target = "systemName", source = "documentType")
    void updateDocument(DocumentType document, @MappingTarget() DocumentTypeEntity documentEntity);
}
