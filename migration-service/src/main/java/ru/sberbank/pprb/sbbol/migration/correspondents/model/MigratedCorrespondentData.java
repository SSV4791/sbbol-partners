package ru.sberbank.pprb.sbbol.migration.correspondents.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Данные мигрированного контрагнета
 */
public class MigratedCorrespondentData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Идентификатор в ППРБ
     */
    private String pprbGuid;
    /**
     * Идентификатор в Legacy СББОЛ
     */
    private String sbbolReplicationGuid;
    /**
     * Версия
     */
    private Long version;

    public MigratedCorrespondentData() {
    }

    public MigratedCorrespondentData(String pprbGuid, String sbbolReplicationGuid, Long version) {
        this.pprbGuid = pprbGuid;
        this.sbbolReplicationGuid = sbbolReplicationGuid;
        this.version = version;
    }

    public String getPprbGuid() {
        return pprbGuid;
    }

    public void setPprbGuid(String pprbGuid) {
        this.pprbGuid = pprbGuid;
    }

    public String getSbbolReplicationGuid() {
        return sbbolReplicationGuid;
    }

    public void setSbbolReplicationGuid(String sbbolReplicationGuid) {
        this.sbbolReplicationGuid = sbbolReplicationGuid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
