package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactDocumentServiceImpl extends DocumentServiceImpl {

    private final ContactRepository contactRepository;

    public ContactDocumentServiceImpl(
        ContactRepository contactRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        super(documentRepository, documentDictionaryRepository, documentMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public DocumentResponse saveDocument(DocumentCreate document) {
        var contact = contactRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getUnifiedId()));
        if (contact == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, document.getDigitalId());
        }
        return super.saveDocument(document);
    }
}
