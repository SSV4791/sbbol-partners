package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentCertifierType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreateFullModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Loggable
@Mapper(
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

    default List<DocumentEntity> toDocuments(Set<DocumentCreateFullModel> documents, String digitalId, UUID unifiedUuid) {
        if (CollectionUtils.isEmpty(documents)) {
            return Collections.emptyList();
        }
        return documents.stream()
            .map(value -> toDocument(value, digitalId, unifiedUuid))
            .collect(Collectors.toList());
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "certifierType", source = "document.certifierType", qualifiedByName = "toCertifierType")
    @Mapping(target = "typeUuid", expression = "java(mapUuid(document.getDocumentTypeId()))")
    @Mapping(target = "series", source = "document.series")
    @Mapping(target = "number", source = "document.number")
    @Mapping(target = "dateIssue", source = "document.dateIssue")
    @Mapping(target = "divisionIssue", source = "document.divisionIssue")
    @Mapping(target = "divisionCode", source = "document.divisionCode")
    @Mapping(target = "certifierName", source = "document.certifierName")
    @Mapping(target = "positionCertifier", source = "document.positionCertifier")
    DocumentEntity toDocument(DocumentCreateFullModel document, String digitalId, UUID unifiedUuid);

    DocumentChange toDocument(DocumentChangeFullModel document, String digitalId, String unifiedId);

    DocumentCreate toDocumentCreate(DocumentChangeFullModel document, String digitalId, String unifiedId);

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

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(document.getUnifiedId()))")
    @Mapping(target = "typeUuid", expression = "java(mapUuid(document.getDocumentTypeId()))")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    void updateDocument(DocumentChange document, @MappingTarget DocumentEntity documentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId", qualifiedByName = "toUUID")
    @Mapping(target = "typeUuid", source = "documentTypeId", qualifiedByName = "toUUID")
    @Mapping(target = "certifierType", source = "certifierType", qualifiedByName = "toCertifierType")
    void patchDocument(DocumentChange document, @MappingTarget DocumentEntity documentEntity);

    @Named("toUUID")
    default UUID toUUID(String id) {
        return mapUuid(id);
    }
}
