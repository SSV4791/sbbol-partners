package ru.sberbank.pprb.sbbol.partners.entity.partner;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serial;

@MappedSuperclass
abstract class PhoneBaseEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "phone", length = 50)
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
