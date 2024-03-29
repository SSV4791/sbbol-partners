package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;

@Loggable
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentDictionaryRepository dictionaryRepository;
    private final DocumentTypeMapper documentTypeMapper;

    public DocumentTypeServiceImpl(DocumentDictionaryRepository dictionaryRepository, DocumentTypeMapper documentTypeMapper) {
        this.dictionaryRepository = dictionaryRepository;
        this.documentTypeMapper = documentTypeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsTypeResponse getDocuments(DocumentTypeFilter filter) {
        var response = dictionaryRepository.findByFilter(filter);
        var documentsTypeResponse = new DocumentsTypeResponse();
        documentsTypeResponse.documentType(documentTypeMapper.toDocumentType(response));
        var pagination = filter.getPagination();
        documentsTypeResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            documentsTypeResponse.getPagination().hasNextPage(Boolean.TRUE);
            documentsTypeResponse.getDocumentType().remove(size - 1);
        }
        return documentsTypeResponse;
    }
}
