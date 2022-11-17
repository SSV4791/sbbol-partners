package ru.sberbank.pprb.sbbol.partners.migration.model;

import java.io.Serializable;
import java.time.LocalDate;

public class MigrateGkuDeleteRequest implements Serializable {

    /**
     * Дата миграции
     */
    private final LocalDate migrateDate;

    public MigrateGkuDeleteRequest(LocalDate migrateDate) {
        this.migrateDate = migrateDate;
    }

    public LocalDate getMigrateDate() {
        return migrateDate;
    }
}

