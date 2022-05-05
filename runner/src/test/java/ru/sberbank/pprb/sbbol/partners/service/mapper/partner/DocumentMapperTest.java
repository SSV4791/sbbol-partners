package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import io.qameta.allure.AllureId;
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
    @AllureId("34050")
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
    @AllureId("34067")
    void toCertifierType() {
        CertifierType typeEnum = factory.manufacturePojo(CertifierType.class);
        DocumentCertifierType documentType = DocumentMapper.toCertifierType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(DocumentMapper.toCertifierType(documentType));
    }
}