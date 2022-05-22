package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.DocumentTypeDictionaryApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeService;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentTypeCreateValidationImpl;

@RestController
public class DocumentDictionaryController implements DocumentTypeDictionaryApi {

    private final DocumentTypeService documentTypeService;

    public DocumentDictionaryController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @Override
    public ResponseEntity<DocumentTypeResponse> create(
        @Validation(type = DocumentTypeCreateValidationImpl.class) DocumentTypeCreate documentTypeCreate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentTypeService.saveDocument(documentTypeCreate));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        documentTypeService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<DocumentsTypeResponse> list(DocumentTypeFilter filter) {
        return ResponseEntity.ok(documentTypeService.getDocuments(filter));
    }

    @Override
    public ResponseEntity<DocumentTypeResponse> update(DocumentTypeChange documentTypeChange) {
        return ResponseEntity.ok(documentTypeService.updateDocument(documentTypeChange));
    }
}
