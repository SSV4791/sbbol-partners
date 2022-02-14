package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.BudgetMaskDictionaryViewRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetMaskDictionaryRepository extends CrudRepository<BudgetMaskEntity, UUID>, BudgetMaskDictionaryViewRepository {

    /**
     * Получение маски бюджетности
     *
     * @param uuid Идентификатор маски
     * @return маска бюджетности
     */
    BudgetMaskEntity getByUuid(UUID uuid);

    /**
     * Получение маски бюджетности
     *
     * @param type Тип Маски
     * @return Маски
     */
    List<BudgetMaskEntity> findAllByType(BudgetMaskType type);

    /**
     * Получение маски бюджетности
     *
     * @return Маски
     */
    List<BudgetMaskEntity> findAll();
}
