package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.mapper.ValidationMapper;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationMapperTest extends BaseUnitConfiguration {
    private static final ValidationMapper validationMapper = Mappers.getMapper(ValidationMapper.class);

    @Test
    void toDescriptionErrors() {
        var errors = new HashMap<String, List<String>>();
        errors.put("1", List.of("1", "2", "3"));
        errors.put("2", List.of("1", "2", "3"));
        var exe = new ModelValidationException(errors);
        var desc = validationMapper.toDescriptions(exe);
        assertThat(desc.get(0).getField()).isEqualTo("1");
        assertThat(desc.get(0).getMessage()).isEqualTo(List.of("1", "2", "3"));
        assertThat(desc.get(1).getField()).isEqualTo("2");
        assertThat(desc.get(1).getMessage()).isEqualTo(List.of("1", "2", "3"));
    }
}
