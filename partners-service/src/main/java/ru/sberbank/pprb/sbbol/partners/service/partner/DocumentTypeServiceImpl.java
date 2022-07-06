package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;

import java.util.UUID;

@Loggable
public class DocumentTypeServiceImpl implements DocumentTypeService {
    private static final String DOCUMENT_TYPE = "document_type";

    private final DocumentDictionaryRepository dictionaryRepository;
    private final DocumentTypeMapper documentTypeMapper;

    public DocumentTypeServiceImpl(DocumentDictionaryRepository dictionaryRepository, DocumentTypeMapper documentTypeMapper) {
        this.dictionaryRepository = dictionaryRepository;
        this.documentTypeMapper = documentTypeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsTypeResponse getDocuments(DocumentTypeFilter filter) {
        var response = dictionaryRepository.findByFilter(filter);
        var documentsTypeResponse = new DocumentsTypeResponse();
        documentsTypeResponse.documentType(documentTypeMapper.toDocumentType(response));
        var pagination = filter.getPagination();
        documentsTypeResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            documentsTypeResponse.getPagination().hasNextPage(Boolean.TRUE);
            documentsTypeResponse.getDocumentType().remove(size - 1);
        }
        return documentsTypeResponse;
    }

    @Override
    @Transactional
    public DocumentTypeResponse saveDocument(DocumentTypeCreate document) {
        DocumentTypeEntity saveDocument = documentTypeMapper.toDocumentType(document);
        DocumentTypeEntity response = dictionaryRepository.save(saveDocument);
        return new DocumentTypeResponse().documentType(documentTypeMapper.toDocumentType(response));
    }

    @Override
    @Transactional
    public DocumentTypeResponse updateDocument(DocumentTypeChange documentTypeChange) {
        DocumentTypeEntity foundDocument = dictionaryRepository.getByUuid(UUID.fromString(documentTypeChange.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_TYPE, documentTypeChange.getId()));
        documentTypeMapper.updateDocument(documentTypeChange, foundDocument);
        dictionaryRepository.save(foundDocument);
        return new DocumentTypeResponse().documentType(documentTypeMapper.toDocumentType(foundDocument));
    }

    @Override
    @Transactional
    public void deleteDocument(String id) {
        DocumentTypeEntity foundDocument = dictionaryRepository.getByUuid(UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_TYPE, id));
        foundDocument.setDeleted(Boolean.TRUE);
        dictionaryRepository.save(foundDocument);
    }
}
