package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentCertifierType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        DocumentTypeMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface DocumentMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(document.getUuid() == null ? null : document.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(document.getUnifiedUuid().toString())")
    @Mapping(target = "documentType", source = "type")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    Document toDocument(DocumentEntity document);

    @Named("toCertifierType")
    static CertifierType toCertifierType(DocumentCertifierType certifierType) {
        return certifierType != null ? CertifierType.valueOf(certifierType.name()) : null;
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedId()))")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    @Mapping(target = "typeUuid", expression = "java(mapUuid(document.getDocumentTypeId()))")
    DocumentEntity toDocument(DocumentCreate document);

    @Named("toCertifierType")
    static DocumentCertifierType toCertifierType(CertifierType certifierType) {
        return certifierType != null ? DocumentCertifierType.valueOf(certifierType.getValue()) : null;
    }

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(document.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedId()))")
    @Mapping(target = "typeUuid", expression = "java(mapUuid(document.getDocumentTypeId()))")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    void updateDocument(DocumentChange document, @MappingTarget() DocumentEntity documentEntity);
}
