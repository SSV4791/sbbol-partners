package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.DocumentTypeDictionaryApi;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeService;

@Loggable
@RestController
public class DocumentDictionaryController implements DocumentTypeDictionaryApi {

    private final DocumentTypeService documentTypeService;

    public DocumentDictionaryController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @Override
    public ResponseEntity<DocumentsTypeResponse> list(DocumentTypeFilter filter) {
        return ResponseEntity.ok(documentTypeService.getDocuments(filter));
    }
}
