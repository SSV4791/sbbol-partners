package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentDictionaryRepository extends CrudRepository<DocumentTypeEntity, UUID> {

    /**
     * Получение типа документа по идунтефикатору
     *
     * @param id Идентефикатор документа
     * @return Документ
     */
    DocumentTypeEntity getById(UUID id);

    /**
     * Получение типов документов по фильтру
     *
     * @param deleted тип получаемых документов
     * @return Документы
     */
    List<DocumentTypeEntity> findAllByDeleted(Boolean deleted);
}
