package ru.sberbank.pprb.sbbol.partners.repository.renter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.renter.Renter;

import java.util.List;

@Repository
public interface RenterRepository extends PagingAndSortingRepository<Renter, String> {

    /**
     * Получение Арендадателей по uuid
     *
     * @param uuid идентификатор документа
     */
    List<Renter> findAllByUuid(String uuid);

    /**
     * Запрос Арендаторов по uuid и digitalId
     *
     * @param uuid      уникальная запись документа
     * @param digitalId идентификатор личного кабинета клиента
     */
    List<Renter> findAllByUuidAndDigitalId(String uuid, String digitalId);

    /**
     * Запрос документов по digitalId с сортировкой
     *
     * @param digitalId идентификатор личного кабинета клиента
     * @param sort      параметры сортировки
     */
    Iterable<Renter> findAllByDigitalId(String digitalId, Sort sort);

    /**
     * Запрос документов по digitalId с пагинацией
     *
     * @param digitalId идентификатор личного кабинета пользователя
     * @param pageable  параметры пагинации
     */
    Page<Renter> findAllByDigitalId(String digitalId, Pageable pageable);
}
