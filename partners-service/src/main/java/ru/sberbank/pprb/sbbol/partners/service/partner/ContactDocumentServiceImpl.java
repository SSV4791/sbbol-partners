package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

@Loggable
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
    public Document saveDocument(DocumentCreate document) {
        var contact = contactRepository.getByDigitalIdAndUuid(document.getDigitalId(), document.getUnifiedId());
        if (contact.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, document.getDigitalId());
        }
        return super.saveDocument(document);
    }
}
