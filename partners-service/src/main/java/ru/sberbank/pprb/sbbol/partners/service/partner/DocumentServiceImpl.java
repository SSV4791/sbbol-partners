package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.UUID;

abstract class DocumentServiceImpl implements DocumentService {

    public static final String DOCUMENT_NAME = "document";

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(
        DocumentRepository documentRepository,
        DocumentMapper documentMapper
    ) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    public DocumentResponse getDocument(String digitalId, String id) {
        var document = documentRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (document == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        var response = documentMapper.toDocument(document);
        return new DocumentResponse().document(response);
    }

    public DocumentsResponse getDocuments(DocumentsFilter documentsFilter) {
        var response = documentRepository.findByFilter(documentsFilter);
        var documentsResponse = new DocumentsResponse();
        for (var entity : response) {
            documentsResponse.addDocumentsItem(documentMapper.toDocument(entity));
        }
        documentsResponse.setPagination(
            new Pagination()
                .offset(documentsFilter.getPagination().getOffset())
                .count(documentsFilter.getPagination().getCount())
        );
        return documentsResponse;
    }

    public DocumentResponse saveDocument(Document document) {
        var requestDocument = documentMapper.toDocument(document);
        var saveDocument = documentRepository.save(requestDocument);
        var response = documentMapper.toDocument(saveDocument);
        return new DocumentResponse().document(response);
    }

    public DocumentResponse updateDocument(Document document) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getId()));
        if (foundDocument == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, document.getDigitalId(), document.getId());
        }
        documentMapper.updateDocument(document, foundDocument);
        var saveContact = documentRepository.save(foundDocument);
        var response = documentMapper.toDocument(saveContact);
        return new DocumentResponse().document(response);
    }

    public void deleteDocument(String digitalId, String id) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundDocument == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        documentRepository.delete(foundDocument);
    }
}
