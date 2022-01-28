package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentDictionaryRepository extends CrudRepository<DocumentTypeEntity, UUID> {

    /**
     * Получение типа документа по идентификатору
     *
     * @param uuid идентификатор документа
     * @return Тип документа
     */
    DocumentTypeEntity getByUuid(UUID uuid);

    /**
     * Получение типа документа по системному наименованию
     *
     * @param systemName Системное имя документа
     * @return Тип документа
     */
    DocumentTypeEntity getBySystemName(String systemName);

    /**
     * Получение типов документов по фильтру
     *
     * @param deleted тип получаемых документов
     * @return Типы документов
     */
    List<DocumentTypeEntity> findAllByDeleted(Boolean deleted);
}
