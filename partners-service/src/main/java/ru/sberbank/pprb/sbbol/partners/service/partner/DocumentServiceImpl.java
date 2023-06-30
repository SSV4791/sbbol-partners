package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.mapUuid;

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
        var foundDocument = findDocumentEntity(document.getDigitalId(), document.getId(), document.getVersion(), document.getDocumentTypeId());
        documentMapper.updateDocument(document, foundDocument);
        return saveDocument(foundDocument);
    }

    @Override
    @Transactional
    public Document patchDocument(DocumentChange document) {
        var foundDocument = findDocumentEntity(document.getDigitalId(), document.getId(), document.getVersion(), document.getDocumentTypeId());
        documentMapper.patchDocument(document, foundDocument);
        return saveDocument(foundDocument);
    }

    @Override
    @Transactional
    public void deleteDocuments(String digitalId, List<String> ids) {
        for (String id : ids) {
            var uuid = mapUuid(id);
            var foundDocument = documentRepository.getByDigitalIdAndUuid(digitalId, uuid);
            if (foundDocument.isEmpty()) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, uuid);
            }
            documentRepository.delete(foundDocument.get());
        }
    }

    @Override
    @Transactional
    public void saveOrPatchDocuments(String digitalId, String partnerId, Set<DocumentChangeFullModel> documents) {
        Optional.ofNullable(documents)
            .ifPresent(documentList ->
                documentList.forEach(documentChangeFullModel -> saveOrPatchDocument(digitalId, partnerId, documentChangeFullModel)));
    }

    @Override
    @Transactional
    public void saveOrPatchDocument(String digitalId, String partnerId, DocumentChangeFullModel documentChangeFullModel) {
        if (StringUtils.hasText(documentChangeFullModel.getId())) {
            var document = documentMapper.toDocument(documentChangeFullModel, digitalId, partnerId);
            patchDocument(document);
        } else {
            var documentCreate = documentMapper.toDocumentCreate(documentChangeFullModel, digitalId, partnerId);
            saveDocument(documentCreate);
        }
    }

    private DocumentEntity findDocumentEntity(String digitalId, String documentId, Long version, String documentTypeId) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(documentId))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, documentId));
        if (!Objects.equals(version, foundDocument.getVersion())) {
            throw new OptimisticLockException(foundDocument.getVersion(), version);
        }
        if (StringUtils.hasText(documentTypeId)) {
            var foundDocumentType =
                documentDictionaryRepository.getByUuid(UUID.fromString(documentTypeId));
            if (foundDocumentType.isEmpty()) {
                throw new EntryNotFoundException("documentType", digitalId, documentId);
            }
        }
        return foundDocument;
    }

    private Document saveDocument(DocumentEntity document) {
        var saveContact = documentRepository.save(document);
        var response = documentMapper.toDocument(saveContact);
        response.setVersion(response.getVersion() + 1);
        return response;
    }
}
