package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

@Loggable
public class PartnerDocumentServiceImpl extends DocumentServiceImpl {

    private final PartnerService partnerService;

    public PartnerDocumentServiceImpl(
        PartnerService partnerService,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        super(documentRepository, documentDictionaryRepository, documentMapper);
        this.partnerService = partnerService;
    }

    @Override
    @Transactional
    public Document saveDocument(DocumentCreate document) {
        var partnerId = document.getUnifiedId();
        partnerService.existsPartner(document.getDigitalId(), partnerId);
        return super.saveDocument(document);
    }
}
