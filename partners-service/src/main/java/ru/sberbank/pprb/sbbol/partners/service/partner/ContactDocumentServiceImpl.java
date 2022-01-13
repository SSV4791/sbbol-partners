package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactDocumentServiceImpl extends DocumentServiceImpl {

    private final ContactRepository contactRepository;

    public ContactDocumentServiceImpl(
        ContactRepository contactRepository,
        DocumentRepository documentRepository,
        DocumentMapper documentMapper
    ) {
        super(documentRepository, documentMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocument(String digitalId, String id) {
        return super.getDocument(digitalId, id);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsResponse getDocuments(DocumentsFilter documentsFilter) {
        return super.getDocuments(documentsFilter);
    }

    @Override
    @Transactional
    public DocumentResponse saveDocument(Document document) {
        var contact = contactRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getUnifiedId()));
        if (contact == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, document.getDigitalId(), document.getId());
        }
        return super.saveDocument(document);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(Document document) {
        return super.updateDocument(document);
    }

    @Override
    @Transactional
    public void deleteDocument(String digitalId, String id) {
        super.deleteDocument(digitalId, id);
    }
}
