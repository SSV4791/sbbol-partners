package ru.sberbank.pprb.sbbol.partners.aspect.validation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;

import java.util.ArrayList;
import java.util.List;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ValidationMapper {

    default List<Descriptions> toDescriptions(ModelValidationException modelValidationException) {
        List<Descriptions> listDesc = new ArrayList<>();
        for (var error : modelValidationException.getErrors().entrySet()) {
            var desc = new Descriptions();
            desc.field(error.getKey());
            desc.message(error.getValue());
            listDesc.add(desc);
        }
        return listDesc;
    }
}

