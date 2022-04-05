package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeLegalFormEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import static org.assertj.core.api.Assertions.assertThat;

class LegalFormMapperTest  extends BaseConfiguration {

    private static final LegalFormMapper mapper = Mappers.getMapper(LegalFormMapper.class);

    @Test
    @AllureId("37073")
    void testToDocumentTypeLegalFormEntity() {
        LegalForm expected = factory.manufacturePojo(LegalForm.class);
        DocumentTypeLegalFormEntity actual = mapper.toDocumentTypeLegalFormEntity(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toLegalForm(actual));
    }
}
