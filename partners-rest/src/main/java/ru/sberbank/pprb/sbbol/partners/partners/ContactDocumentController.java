package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.ContactDocumentApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentService;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentsFilterValidationImpl;

@RestController
public class ContactDocumentController implements ContactDocumentApi {

    private final DocumentService contactDocumentService;

    public ContactDocumentController(DocumentService contactDocumentService) {
        this.contactDocumentService = contactDocumentService;
    }

    @Override
    public ResponseEntity<Document> create(@Validation(type = DocumentCreateValidationImpl.class) DocumentCreate document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactDocumentService.saveDocument(document));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        contactDocumentService.deleteDocument(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Document> getById(String digitalId, String id) {
        return ResponseEntity.ok(contactDocumentService.getDocument(digitalId, id));
    }

    @Override
    public ResponseEntity<DocumentsResponse> list(@Validation(type = DocumentsFilterValidationImpl.class) DocumentsFilter documentsFilter) {
        return ResponseEntity.ok(contactDocumentService.getDocuments(documentsFilter));
    }

    @Override
    public ResponseEntity<Document> update(@Validation(type = DocumentUpdateValidationImpl.class) DocumentChange document) {
        return ResponseEntity.ok(contactDocumentService.updateDocument(document));
    }
}
