package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentCertifierType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        DocumentTypeMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface DocumentMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(document.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(document.getUnifiedUuid().toString())")
    @Mapping(target = "documentType", source = "type")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    Document toDocument(DocumentEntity document);

    @Named("toCertifierType")
    static Document.CertifierTypeEnum toCertifierType(DocumentCertifierType certifierType) {
        return certifierType != null ? Document.CertifierTypeEnum.valueOf(certifierType.name()) : null;
    }

    @Mapping(target = "uuid", expression = "java(mapUuid(document.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedId()))")
    @Mapping(target = "type", source = "documentType")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    @Mapping(target = "typeUuid", expression = "java(mapUuid(document.getDocumentType().getId()))")
    DocumentEntity toDocument(Document document);

    @Named("toCertifierType")
    static DocumentCertifierType toCertifierType(Document.CertifierTypeEnum certifierType) {
        return certifierType != null ? DocumentCertifierType.valueOf(certifierType.getValue()) : null;
    }

    @Named("updateDocument")
    @Mapping(target = "uuid", expression = "java(mapUuid(document.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedId()))")
    @Mapping(target = "type", source = "documentType")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    void updateDocument(Document document, @MappingTarget() DocumentEntity documentEntity);
}
