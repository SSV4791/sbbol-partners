package ru.sberbank.pprb.sbbol.migration.correspondents.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Ответ по миграции корреспондента
 */
public class MigrationCorrespondentResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Данные контрагентов прошедшие миграцию
     */
    private List<MigratedCorrespondentData> correspondents;

    public MigrationCorrespondentResponse() {
    }

    public MigrationCorrespondentResponse(List<MigratedCorrespondentData> correspondents) {
        this.correspondents = correspondents;
    }

    public List<MigratedCorrespondentData> getCorrespondents() {
        return correspondents;
    }

    public void setCorrespondents(List<MigratedCorrespondentData> correspondents) {
        this.correspondents = correspondents;
    }
}
