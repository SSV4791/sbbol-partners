package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

abstract class DocumentServiceImpl implements DocumentService {

    public static final String DOCUMENT_NAME = "document";

    private final DocumentRepository documentRepository;
    private final DocumentDictionaryRepository documentDictionaryRepository;
    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        this.documentRepository = documentRepository;
        this.documentDictionaryRepository = documentDictionaryRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Document getDocument(String digitalId, String id) {
        var document = documentRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        return documentMapper.toDocument(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsResponse getDocuments(DocumentsFilter documentsFilter) {
        var response = documentRepository.findByFilter(documentsFilter);
        var documentsResponse = new DocumentsResponse();
        for (var entity : response) {
            documentsResponse.addDocumentsItem(documentMapper.toDocument(entity));
        }
        var pagination = documentsFilter.getPagination();
        documentsResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            documentsResponse.getPagination().hasNextPage(Boolean.TRUE);
            documentsResponse.getDocuments().remove(size - 1);
        }
        return documentsResponse;
    }

    @Override
    @Transactional
    public Document saveDocument(DocumentCreate document) {
        var requestDocument = documentMapper.toDocument(document);
        if (requestDocument.getTypeUuid() != null) {
            var documentType = documentDictionaryRepository.getByUuid(requestDocument.getTypeUuid())
                .orElseThrow(() -> new EntryNotFoundException("documentType", requestDocument.getTypeUuid()));
            requestDocument.setType(documentType);
        }
        var saveDocument = documentRepository.save(requestDocument);
        return documentMapper.toDocument(saveDocument);
    }

    @Override
    @Transactional
    public Document updateDocument(DocumentChange document) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, document.getDigitalId(), document.getId()));
        if (!Objects.equals(document.getVersion(), foundDocument.getVersion())) {
            throw new OptimisticLockException(foundDocument.getVersion(), document.getVersion());
        }
        if (StringUtils.isNotEmpty(document.getDocumentTypeId())) {
            var foundDocumentType =
                documentDictionaryRepository.getByUuid(UUID.fromString(document.getDocumentTypeId()));
            if (foundDocumentType.isPresent()) {
                throw new EntryNotFoundException("documentType", document.getDigitalId(), document.getId());
            }
        }
        documentMapper.updateDocument(document, foundDocument);
        var saveContact = documentRepository.save(foundDocument);
        return documentMapper.toDocument(saveContact);
    }

    @Override
    @Transactional
    public void deleteDocuments(String digitalId, List<String> ids) {
        for (String id : ids) {
            var uuid = documentMapper.mapUuid(id);
            var foundDocument = documentRepository.getByDigitalIdAndUuid(digitalId, uuid);
            if (foundDocument.isEmpty()) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            documentRepository.delete(foundDocument.get());
        }
    }
}
