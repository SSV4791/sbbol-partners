package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentTypeMapperTest extends BaseUnitConfiguration {

    private static final DocumentTypeMapper mapper = Mappers.getMapper(DocumentTypeMapper.class);

    @Test
    @AllureId("34107")
    void testToDocumentType() {
        DocumentType expected = factory.manufacturePojo(DocumentType.class);
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toDocumentType(actual));
    }
}
