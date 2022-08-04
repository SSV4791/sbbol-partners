package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeLegalFormEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {LegalFormMapper.class},
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

    @Mapping(target = "uuid", expression = "java(mapUuid(documentType.getId()))")
    @Mapping(target = "systemName", source = "documentType")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    DocumentTypeEntity toDocumentType(DocumentTypeChange documentType);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "systemName", source = "documentType")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    DocumentTypeEntity toDocumentType(DocumentTypeCreate documentType);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "systemName", source = "documentType")
    @Mapping(target = "legalForms", expression = "java(updateLegalForms(documentType, documentEntity))")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateDocument(DocumentTypeChange documentType, @MappingTarget() DocumentTypeEntity documentEntity);

    default List<DocumentTypeLegalFormEntity> updateLegalForms(DocumentTypeChange documentTypeChange, DocumentTypeEntity documentEntity) {
        if (isNull(documentTypeChange)) {
            return emptyList();
        }
        if (!CollectionUtils.isEmpty(documentTypeChange.getLegalForms())) {
            List<DocumentTypeLegalFormEntity> excludedSubjects = documentEntity.getLegalForms().stream()
                .filter(checkedSubject -> documentTypeChange.getLegalForms().isEmpty() || documentTypeChange.getLegalForms().stream()
                    .noneMatch(targetedSubject -> targetedSubject == (LegalForm.valueOf(checkedSubject.getLegalForm()))))
                .collect(Collectors.toList());
            documentEntity.getLegalForms().removeAll(excludedSubjects);

            List<DocumentTypeLegalFormEntity> includedSubjects = documentTypeChange.getLegalForms().stream()
                .filter(checkedSubject -> documentEntity.getLegalForms().stream()
                    .noneMatch(targetedSubject ->
                        checkedSubject == LegalForm.valueOf(targetedSubject.getLegalForm())))
                .map(subject -> {
                    var result = new DocumentTypeLegalFormEntity();
                    result.setLegalForm(subject.getValue());
                    result.setDocumentType(documentEntity);
                    return result;
                })
                .collect(Collectors.toList());
            documentEntity.getLegalForms().addAll(includedSubjects);
        }
        return documentEntity.getLegalForms();
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget DocumentTypeEntity documentTypeEntity) {
        var documentTypeLegalFormEntities = documentTypeEntity.getLegalForms();
        if (documentTypeLegalFormEntities != null) {
            for (var documentTypeLegalFormEntity : documentTypeLegalFormEntities) {
                documentTypeLegalFormEntity.setDocumentType(documentTypeEntity);
            }
        }
    }
}
