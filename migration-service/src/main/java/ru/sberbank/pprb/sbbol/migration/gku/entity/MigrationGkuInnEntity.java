package ru.sberbank.pprb.sbbol.migration.gku.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(
    name = "gku_inn_dictionary",
    indexes = {
        @Index(name = "i_gku_inn_dictionary_inn", columnList = "inn")
    }
)
public class MigrationGkuInnEntity extends MigrationBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "inn", nullable = false, length = 12)
    private String inn;

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
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
