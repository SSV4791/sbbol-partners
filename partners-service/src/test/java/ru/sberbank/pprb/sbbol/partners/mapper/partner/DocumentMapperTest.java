package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentCertifierType;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Document;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class DocumentMapperTest extends BaseConfiguration {

    private DocumentMapper mapper;

    @Mock
    private DocumentTypeMapper documentTypeMapper;

    @BeforeEach
    void before() {
        mapper = new DocumentMapperImpl(documentTypeMapper);
    }

    @Test
    void testToDocument() {
        Document expected = factory.manufacturePojo(Document.class);
        DocumentEntity actual = mapper.toDocument(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("documentType")
            .isEqualTo(mapper.toDocument(actual));
    }

    @Test
    void toCertifierType() {
        Document.CertifierTypeEnum typeEnum = factory.manufacturePojo(Document.CertifierTypeEnum.class);
        DocumentCertifierType documentType = DocumentMapper.toCertifierType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(DocumentMapper.toCertifierType(documentType));
    }
}
