package ru.sberbank.pprb.sbbol.partners.migration.model;

import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;

import java.io.Serializable;
import java.util.Collection;

public class MigrateGkuRequest implements Serializable {

    /**
     * Список ЖКУ инн
     */
    private final Collection<MigrationGkuCandidate> gkuInns;

    public MigrateGkuRequest(Collection<MigrationGkuCandidate> gkuInns) {
        this.gkuInns = gkuInns;
    }

    public Collection<MigrationGkuCandidate> getGkuInns() {
        return gkuInns;
    }
}
