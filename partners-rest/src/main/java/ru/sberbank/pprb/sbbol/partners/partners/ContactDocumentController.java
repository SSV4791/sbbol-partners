package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.ContactDocumentApi;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactDocumentService;

@RestController
public class ContactDocumentController implements ContactDocumentApi {

    private final ContactDocumentService contactDocumentService;

    public ContactDocumentController(ContactDocumentService contactDocumentService) {
        this.contactDocumentService = contactDocumentService;
    }

    @Override
    public ResponseEntity<DocumentResponse> change(Document document) {
        return ResponseEntity.ok(contactDocumentService.saveDocument(document));
    }

    @Override
    public ResponseEntity<Error> delete(String digitalId, String id) {
        return ResponseEntity.ok(contactDocumentService.deleteDocument(digitalId, id));
    }

    @Override
    public ResponseEntity<DocumentResponse> getById(String digitalId, String id) {
        return ResponseEntity.ok(contactDocumentService.getDocument(digitalId, id));
    }

    @Override
    public ResponseEntity<DocumentsResponse> list(DocumentsFilter documentsFilter) {
        return ResponseEntity.ok(contactDocumentService.getDocuments(documentsFilter));
    }

    @Override
    public ResponseEntity<DocumentResponse> update(Document document) {
        return ResponseEntity.ok(contactDocumentService.updateDocument(document));
    }
}
