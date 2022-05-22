package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentCreateValidationImpl;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerDocumentServiceImpl extends DocumentServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerDocumentServiceImpl(
        PartnerRepository partnerRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        super(documentRepository, documentDictionaryRepository, documentMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional
    public DocumentResponse saveDocument(@Validation(type = DocumentCreateValidationImpl.class) DocumentCreate document) {
        var partner = partnerRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getUnifiedId()));
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", document.getDigitalId());
        }
        return super.saveDocument(document);
    }
}
