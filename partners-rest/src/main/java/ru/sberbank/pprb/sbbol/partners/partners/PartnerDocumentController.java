package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerDocumentApi;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentService;

@RestController
public class PartnerDocumentController implements PartnerDocumentApi {

    private final DocumentService partnerDocumentService;

    public PartnerDocumentController(DocumentService partnerDocumentService) {
        this.partnerDocumentService = partnerDocumentService;
    }

    @Override
    public ResponseEntity<DocumentResponse> create(DocumentCreate document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerDocumentService.saveDocument(document));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        partnerDocumentService.deleteDocument(digitalId, id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<DocumentResponse> update(DocumentChange document) {
        return ResponseEntity.ok(partnerDocumentService.updateDocument(document));
    }
}
