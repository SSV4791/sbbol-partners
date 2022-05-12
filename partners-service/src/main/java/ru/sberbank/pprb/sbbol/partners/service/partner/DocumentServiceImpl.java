package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

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
    public DocumentResponse getDocument(String digitalId, String id) {
        var document = documentRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = documentMapper.toDocument(document);
        return new DocumentResponse().document(response);
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
    public DocumentResponse saveDocument(DocumentCreate document) {
        var requestDocument = documentMapper.toDocument(document);
        if (requestDocument.getTypeUuid() != null) {
            var documentType = documentDictionaryRepository.getByUuid(requestDocument.getTypeUuid());
            documentType.ifPresent(requestDocument::setType);
        }
        var saveDocument = documentRepository.save(requestDocument);
        var response = documentMapper.toDocument(saveDocument);
        return new DocumentResponse().document(response);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(DocumentChange document) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, document.getDigitalId(), document.getId()));
        if (!document.getVersion().equals(foundDocument.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundDocument.getVersion() +
                " не равна версии записи в запросе version=" + document.getVersion());
        }
        documentMapper.updateDocument(document, foundDocument);
        var saveContact = documentRepository.save(foundDocument);
        var response = documentMapper.toDocument(saveContact);
        return new DocumentResponse().document(response);
    }

    @Override
    @Transactional
    public void deleteDocument(String digitalId, String id) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundDocument.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        documentRepository.delete(foundDocument.get());
    }
}
