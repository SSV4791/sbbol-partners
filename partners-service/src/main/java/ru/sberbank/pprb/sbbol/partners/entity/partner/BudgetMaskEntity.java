package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Table(
    name = "budget_mask_dictionary",
    indexes = {
        @Index(name = "budget_mask_dictionary_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_budget_mask_dictionary_type", columnList = "type")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class BudgetMaskEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "mask", nullable = false, length = 20)
    private String mask;

    @Column(name = "condition", nullable = false, length = 20)
    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private BudgetMaskType type;

    public BudgetMaskType getType() {
        return type;
    }

    public void setType(BudgetMaskType type) {
        this.type = type;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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
        BudgetMaskEntity that = (BudgetMaskEntity) obj;
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
