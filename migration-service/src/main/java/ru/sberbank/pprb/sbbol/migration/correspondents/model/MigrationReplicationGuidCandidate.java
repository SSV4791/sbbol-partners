package ru.sberbank.pprb.sbbol.migration.correspondents.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Кандидат на миграцию
 */
public class MigrationReplicationGuidCandidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Репликационный guid
     */
    private String replicationGuid;

    /**
     * Guid счета
     */
    private String pprbGuid;

    public MigrationReplicationGuidCandidate() {
    }

    public MigrationReplicationGuidCandidate(String replicationGuid, String pprbGuid) {
        this.replicationGuid = replicationGuid;
        this.pprbGuid = pprbGuid;
    }

    public String getReplicationGuid() {
        return replicationGuid;
    }

    public String getPprbGuid() {
        return pprbGuid;
    }
}
