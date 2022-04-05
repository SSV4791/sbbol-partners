package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.DocumentDictionaryViewRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentDictionaryRepository extends CrudRepository<DocumentTypeEntity, UUID>, DocumentDictionaryViewRepository {

    /**
     * Получение типа документа по идентификатору
     *
     * @param uuid идентификатор документа
     * @return Тип документа
     */
    Optional<DocumentTypeEntity> getByUuid(UUID uuid);

    /**
     * Получение типа документа по системному наименованию
     *
     * @param systemName Системное имя документа
     * @return Тип документа
     */
    Optional<DocumentTypeEntity> getBySystemName(String systemName);
}
