package ru.sberbank.pprb.sbbol.migration.gku.model;

import java.io.Serializable;

/**
 * Гку-кандидат для миграции
 */
public class MigrationGkuCandidate implements Serializable {

    /**
     * Инн
     */
    private String inn;

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }
}
