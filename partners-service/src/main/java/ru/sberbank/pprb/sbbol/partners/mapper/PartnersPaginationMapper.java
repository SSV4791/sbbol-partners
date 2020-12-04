package ru.sberbank.pprb.sbbol.partners.mapper;

import org.mapstruct.Mapper;
import ru.sberbank.pprb.sbbol.partners.renter.model.Pagination;

@Mapper(componentModel = "spring")
public interface PartnersPaginationMapper {

    /**
     * Преобразовывает модель данных DataSpace в модель данных ППРБ
     */
    Pagination toFront(ru.sberbank.pprb.sbbol.partners.pagination.Pagination pagination);

    /**
     * Преобразовывает модель данных ППРБ в модель данных DataSpace
     */
    ru.sberbank.pprb.sbbol.partners.pagination.Pagination toBack(Pagination pagination);
}
