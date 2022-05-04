package ru.sberbank.pprb.sbbol.partners.legacy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Модель с данными для сохранения подписи контрагента
 */
public class CounterpartySignData implements Serializable {

    /**
     * GUID контрагента в ППРБ
     */
    private UUID pprbGuid;

    /**
     * ID криптопрофиля
     */
    private Long signProfileId;

    /**
     * Дата подписи
     */
    private Date signDate;

    /**
     * Подпись в формате base64
     */
    private String base64sign;

    /**
     * Дайджест
     */
    private String digest;

    /**
     * Id схемы подписи, например, default
     */
    private String dcsId;

    public UUID getPprbGuid() {
        return pprbGuid;
    }

    public void setPprbGuid(UUID pprbGuid) {
        this.pprbGuid = pprbGuid;
    }

    public Long getSignProfileId() {
        return signProfileId;
    }

    public void setSignProfileId(Long signProfileId) {
        this.signProfileId = signProfileId;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getBase64sign() {
        return base64sign;
    }

    public void setBase64sign(String base64sign) {
        this.base64sign = base64sign;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getDcsId() {
        return dcsId;
    }

    public void setDcsId(String dcsId) {
        this.dcsId = dcsId;
    }
}
