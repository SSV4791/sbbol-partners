package ru.sberbank.pprb.sbbol.partners.entity;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Базовый класс модели
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable, HashKeyProvider {

    /**
     * Прикладной ID объекта
     */
    @Column(name = "OBJECT_ID", nullable = false, length = 254)
    @Id
    @GeneratedValue(generator = "snowflake-generator")
    @GenericGenerator(
        name = "snowflake-generator",
        strategy = "sbp.sbt.model.config.snowflake.SnowflakeIdGenerator"
    )
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
