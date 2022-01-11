package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;

import java.util.List;
import java.util.UUID;

@Service
@Logged(printRequestResponse = true)
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentDictionaryRepository dictionaryRepository;
    private final DocumentTypeMapper documentTypeMapper;

    public DocumentTypeServiceImpl(DocumentDictionaryRepository dictionaryRepository, DocumentTypeMapper documentTypeMapper) {
        this.dictionaryRepository = dictionaryRepository;
        this.documentTypeMapper = documentTypeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsTypeResponse getDocuments(Boolean status) {
        List<DocumentTypeEntity> response = dictionaryRepository.findAllByDeleted(status);
        return new DocumentsTypeResponse().documentType(documentTypeMapper.toDocumentType(response));
    }

    @Override
    @Transactional
    public DocumentTypeResponse saveDocument(DocumentType document) {
        DocumentTypeEntity saveDocument = documentTypeMapper.toDocumentType(document);
        DocumentTypeEntity response = dictionaryRepository.save(saveDocument);
        return new DocumentTypeResponse().documentType(documentTypeMapper.toDocumentType(response));
    }

    @Override
    @Transactional
    public DocumentTypeResponse updateDocument(DocumentType document) {
        DocumentTypeEntity searchDocument = dictionaryRepository.getById(UUID.fromString(document.getUuid()));
        if (searchDocument == null) {
            throw new EntryNotFoundException("contact", document.getUuid());
        }
        documentTypeMapper.updateDocument(document, searchDocument);
        dictionaryRepository.save(searchDocument);
        return new DocumentTypeResponse().documentType(documentTypeMapper.toDocumentType(searchDocument));
    }
}
