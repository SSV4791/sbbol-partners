package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;

import java.util.UUID;

@Repository
public interface GkuInnDictionaryRepository extends CrudRepository<GkuInnEntity, UUID> {

    /**
     * Получение ИНН организаций поставщиков услуг ЖКУ
     *
     * @param inn ИНН
     * @return ИНН организаций поставщиков услуг ЖКУ
     */
    GkuInnEntity getByInn(String inn);
}
