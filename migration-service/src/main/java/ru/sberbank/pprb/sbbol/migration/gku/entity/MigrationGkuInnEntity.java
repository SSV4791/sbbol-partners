package ru.sberbank.pprb.sbbol.migration.gku.entity;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(
    name = "gku_inn_dictionary",
    indexes = {
        @Index(name = "i_gku_inn_dictionary_inn", columnList = "inn")
    }
)
public class MigrationGkuInnEntity implements Serializable, HashKeyProvider {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "uuid", updatable = false, nullable = false)
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID uuid;

    @Column(name = "inn", nullable = false, length = 12)
    private String inn;

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        return getUuid() == null ? super.hashCode() : Objects.hash(getUuid());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MigrationGkuInnEntity that = (MigrationGkuInnEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return "7352bf20-bab8-4177-9176-33147d0d31d2";
    }
}
