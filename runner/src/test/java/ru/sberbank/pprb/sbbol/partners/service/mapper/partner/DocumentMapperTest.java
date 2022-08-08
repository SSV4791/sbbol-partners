package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentCertifierType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreateFullModel;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentMapperTest extends BaseUnitConfiguration {

    private DocumentMapper mapper;

    @Mock
    private DocumentTypeMapper documentTypeMapper;

    @BeforeEach
    void before() {
        mapper = new DocumentMapperImpl(documentTypeMapper);
    }

    @Test
    void testToDocuments() {
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedUuid = factory.manufacturePojo(UUID.class);
        HashSet<DocumentCreateFullModel> expected = factory.manufacturePojo(HashSet.class, DocumentCreateFullModel.class);
        var actual = mapper.toDocuments(expected, digitalId, unifiedUuid);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .hasSameSizeAs(actual);
        for(var actualObj: actual) {
            assertThat(actualObj.getDigitalId())
                .isEqualTo(digitalId);
            assertThat(unifiedUuid)
                .isEqualTo(actualObj.getUnifiedUuid());
        }
    }

    @Test
    void testToDocumentWithDocumentCreateFullModel() {
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedUuid = factory.manufacturePojo(UUID.class);
        var expected = factory.manufacturePojo(DocumentCreateFullModel.class);
        var actual = mapper.toDocument(expected, digitalId, unifiedUuid);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "documentTypeId"
            )
            .isEqualTo(actual);
        assertThat(actual.getTypeUuid())
            .hasToString(expected.getDocumentTypeId());
    }

    @Test
    void testToDocument() {
        var document = factory.manufacturePojo(DocumentCreate.class);
        DocumentEntity actual = mapper.toDocument(document);
        var expected = mapper.toDocument(actual);
        assertThat(expected)
            .isNotNull();
        assertThat(expected.getNumber()).isEqualTo(actual.getNumber());
        assertThat(expected.getDigitalId()).isEqualTo(actual.getDigitalId());
        assertThat(expected.getVersion()).isEqualTo(actual.getVersion());
        assertThat(expected.getSeries()).isEqualTo(actual.getSeries());
        assertThat(expected.getDateIssue()).isEqualTo(actual.getDateIssue());
        assertThat(expected.getDivisionIssue()).isEqualTo(actual.getDivisionIssue());
        assertThat(expected.getDivisionCode()).isEqualTo(actual.getDivisionCode());
        assertThat(expected.getCertifierName()).isEqualTo(actual.getCertifierName());
        assertThat(expected.getPositionCertifier()).isEqualTo(actual.getPositionCertifier());
    }

    @Test
    void toCertifierType() {
        CertifierType typeEnum = factory.manufacturePojo(CertifierType.class);
        DocumentCertifierType documentType = DocumentMapper.toCertifierType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(DocumentMapper.toCertifierType(documentType));
    }
}
