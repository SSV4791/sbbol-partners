package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
public class GkuInnEntity extends BaseEntity {

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
        GkuInnEntity that = (GkuInnEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getUuid().toString();
    }
}
