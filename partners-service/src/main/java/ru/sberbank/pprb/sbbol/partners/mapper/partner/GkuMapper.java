package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;

@Loggable
@Mapper
public interface GkuMapper {

    @Named("isGku")
    static Boolean isGku(GkuInnEntity isGkuInn) {
        return isGkuInn != null;
    }
}
