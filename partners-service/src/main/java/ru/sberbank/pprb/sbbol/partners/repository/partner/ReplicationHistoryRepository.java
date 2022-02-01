package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ReplicationHistoryEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReplicationHistoryRepository extends CrudRepository<ReplicationHistoryEntity, UUID> {

    /**
     * Получение истории слияния по id партнера
     *
     * @param partnerUuid Идентификатор сущности
     * @return История слияния.
     */
    List<ReplicationHistoryEntity> findByPartnerUuid(UUID partnerUuid);

    /**
     * Получение истории слияния по id счёта
     *
     * @param accountUuid Идентификатор сущности счёта
     * @return История слияния.
     */
    List<ReplicationHistoryEntity> findByAccountUuid(UUID accountUuid);

    /**
     * Получение истории слияния по id сущности из СББОЛа
     *
     * @param sbbolGuid Идентификатор сущности СББОЛа
     * @return История слияния.
     */
    List<ReplicationHistoryEntity> findBySbbolGuid(String sbbolGuid);

    /**
     * Удаление истории слияния по id сущности из СББОЛа
     *
     * @param sbbolGuid Идентификатор сущности СББОЛа
     */
    void deleteBySbbolGuid(String sbbolGuid);

    /**
     * Удаление истории слияния по uuid`у партнёра
     *
     * @param partnerUuid Идентификатор сущности партнёра
     */
    void deleteByPartnerUuid(UUID partnerUuid);
}
