package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerDocumentServiceImpl extends DocumentServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerDocumentServiceImpl(
        PartnerRepository partnerRepository,
        DocumentRepository documentRepository,
        DocumentMapper documentMapper
    ) {
        super(documentRepository, documentMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional
    public DocumentResponse saveDocument(Document document) {
        var partner = partnerRepository.getByDigitalIdAndUuid(document.getDigitalId(), UUID.fromString(document.getUnifiedId()));
        if (partner == null) {
            throw new EntryNotFoundException("partner", document.getDigitalId(), document.getId());
        }
        return super.saveDocument(document);
    }
}
