package ru.sberbank.pprb.sbbol.partners.migration.model;

import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;

import java.io.Serializable;
import java.util.Collection;

// TODO DCBBRAIN-2268 Перенести тесты и модели для migration-service из модуля Runner в свой модуль
/**
 * Запрос для миграции контрагентов
 */
public class MigrateCorrespondentRequest implements Serializable {

    /**
     * Идентификатор организации
     */
    private final String digitalId;

    /**
     * Список контрагентов-кандидатов для миграции
     */
    private final Collection<MigrationCorrespondentCandidate> correspondents;

    public MigrateCorrespondentRequest(String digitalId, Collection<MigrationCorrespondentCandidate> correspondents) {
        this.digitalId = digitalId;
        this.correspondents = correspondents;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public Collection<MigrationCorrespondentCandidate> getCorrespondents() {
        return correspondents;
    }
}
