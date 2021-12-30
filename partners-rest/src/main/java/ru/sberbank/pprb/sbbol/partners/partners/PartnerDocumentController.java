package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerDocumentApi;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerDocumentService;

@RestController
public class PartnerDocumentController implements PartnerDocumentApi {

    private final PartnerDocumentService partnerDocumentService;

    public PartnerDocumentController(PartnerDocumentService partnerDocumentService) {
        this.partnerDocumentService = partnerDocumentService;
    }

    @Override
    public ResponseEntity<DocumentResponse> change(Document document) {
        return ResponseEntity.ok(partnerDocumentService.saveDocument(document));
    }

    @Override
    public ResponseEntity<Error> delete(String digitalId, String id) {
        return ResponseEntity.ok(partnerDocumentService.deleteDocument(digitalId, id));
    }

    @Override
    public ResponseEntity<DocumentResponse> getById(String digitalId, String id) {
        return ResponseEntity.ok(partnerDocumentService.getDocument(digitalId, id));
    }

    @Override
    public ResponseEntity<DocumentsResponse> list(DocumentsFilter documentsFilter) {
        return ResponseEntity.ok(partnerDocumentService.getDocuments(documentsFilter));
    }

    @Override
    public ResponseEntity<DocumentResponse> update(Document document) {
        return ResponseEntity.ok(partnerDocumentService.updateDocument(document));
    }
}
