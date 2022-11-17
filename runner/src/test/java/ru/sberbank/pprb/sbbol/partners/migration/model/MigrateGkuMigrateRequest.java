package ru.sberbank.pprb.sbbol.partners.migration.model;

import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;

import java.io.Serializable;
import java.util.Collection;

public class MigrateGkuMigrateRequest implements Serializable {

    /**
     * Список ЖКУ инн
     */
    private final Collection<MigrationGkuCandidate> gkuInns;

    public MigrateGkuMigrateRequest(Collection<MigrationGkuCandidate> gkuInns) {
        this.gkuInns = gkuInns;
    }

    public Collection<MigrationGkuCandidate> getGkuInns() {
        return gkuInns;
    }
}
