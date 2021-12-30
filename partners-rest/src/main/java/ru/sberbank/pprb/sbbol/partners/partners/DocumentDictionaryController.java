package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.DocumentTypeDictionaryApi;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeService;

@RestController
public class DocumentDictionaryController implements DocumentTypeDictionaryApi {

    private final DocumentTypeService documentTypeService;

    public DocumentDictionaryController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @Override
    public ResponseEntity<DocumentTypeResponse> change(DocumentType documentType) {
        return ResponseEntity.ok(documentTypeService.saveDocument(documentType));
    }

    @Override
    public ResponseEntity<DocumentsTypeResponse> list(Boolean status) {
        return ResponseEntity.ok(documentTypeService.getDocuments(status));
    }

    @Override
    public ResponseEntity<DocumentTypeResponse> update(DocumentType documentType) {
        return ResponseEntity.ok(documentTypeService.updateDocument(documentType));
    }
}
