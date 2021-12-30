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

    @Mapping(target = "uuid", expression = "java(document.getId() != null ? document.getId().toString() : null)")
    @Mapping(target = "unifiedUuid", expression = "java(document.getUnifiedUuid() != null ? document.getUnifiedUuid().toString() : null)")
    @Mapping(target = "documentType", source = "type")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    Document toDocument(DocumentEntity document);

    @Named("toCertifierType")
    static Document.CertifierTypeEnum toCertifierType(DocumentCertifierType certifierType) {
        return certifierType != null ? Document.CertifierTypeEnum.valueOf(certifierType.name()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(document.getUuid()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedUuid()))")
    @Mapping(target = "type", source = "documentType")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    @Mapping(target = "typeUuid", expression = "java(mapUuid(document.getDocumentType().getUuid()))")
    DocumentEntity toDocument(Document document);

    @Named("toCertifierType")
    static DocumentCertifierType toCertifierType(Document.CertifierTypeEnum certifierType) {
        return certifierType != null ? DocumentCertifierType.valueOf(certifierType.getValue()) : null;
    }

    @Named("updateDocument")
    @Mapping(target = "id", expression = "java(mapUuid(document.getUuid()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedUuid()))")
    @Mapping(target = "type", source = "documentType")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    void updateDocument(Document document, @MappingTarget() DocumentEntity documentEntity);
}
