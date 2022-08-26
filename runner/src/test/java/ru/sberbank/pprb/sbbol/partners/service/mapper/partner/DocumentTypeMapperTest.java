package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.LegalFormMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentTypeMapperTest extends BaseUnitConfiguration {

    private final DocumentTypeMapper mapper = new DocumentTypeMapperImpl(new LegalFormMapperImpl());

    @Test
    void testToDocumentTypeEntity() {
        DocumentType expected = factory.manufacturePojo(DocumentType.class);
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toDocumentType(actual));
    }
}
