package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Service
public class PartnerDocumentServiceImpl implements PartnerDocumentService {

    private final PartnerRepository partnerRepository;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public PartnerDocumentServiceImpl(
        PartnerRepository partnerRepository,
        DocumentRepository documentRepository,
        DocumentMapper documentMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocument(String digitalId, String id) {
        var document = documentRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
        var response = documentMapper.toDocument(document);
        return new DocumentResponse().document(response);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsResponse getDocuments(DocumentsFilter documentsFilter) {
        return null;
    }

    @Override
    @Transactional
    public DocumentResponse saveDocument(Document document) {
        var partner = partnerRepository.getByDigitalIdAndId(document.getDigitalId(), UUID.fromString(document.getUnifiedUuid()));
        if (partner == null) {
            return null;
        }
        var requestDocument = documentMapper.toDocument(document);
        var saveDocument = documentRepository.save(requestDocument);
        var response = documentMapper.toDocument(saveDocument);
        return new DocumentResponse().document(response);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(Document document) {
        var searchDocument = documentRepository.getByDigitalIdAndId(document.getDigitalId(), UUID.fromString(document.getUuid()));
        if (searchDocument == null) {
            return null;
        }
        documentMapper.updateDocument(document, searchDocument);
        var saveContact = documentRepository.save(searchDocument);
        var response = documentMapper.toDocument(saveContact);
        return new DocumentResponse().document(response);
    }

    @Override
    @Transactional
    public Error deleteDocument(String digitalId, String id) {
        var searchDocument = documentRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
        if (searchDocument == null) {
            return new Error();
        }
        documentRepository.delete(searchDocument);
        return new Error();
    }
}
