package ru.sberbank.pprb.sbbol.partners.migration.model;

import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationReplicationGuidCandidate;

import java.io.Serializable;
import java.util.List;

public class MigrateReplicationGuidRequest implements Serializable {

    /**
     * Идентификатор организации
     */
    private final String digitalId;

    /**
     * Кандидат на миграцию
     */
    private final List<MigrationReplicationGuidCandidate> candidates;

    public MigrateReplicationGuidRequest(String digitalId, List<MigrationReplicationGuidCandidate> candidates) {
        this.digitalId = digitalId;
        this.candidates = candidates;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public List<MigrationReplicationGuidCandidate> getCandidates() {
        return candidates;
    }
}
