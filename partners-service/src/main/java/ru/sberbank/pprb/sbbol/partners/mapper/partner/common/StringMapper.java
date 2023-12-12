package ru.sberbank.pprb.sbbol.partners.mapper.partner.common;


import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;

@Loggable
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StringMapper {

    @Named("toTrimmed")
    static String toTrimmed(String value) {
        return value != null ? value.trim() : null;
    }
}
