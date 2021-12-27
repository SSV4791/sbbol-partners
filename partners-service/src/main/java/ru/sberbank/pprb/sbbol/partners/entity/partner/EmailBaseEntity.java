package ru.sberbank.pprb.sbbol.partners.entity.partner;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serial;

@MappedSuperclass
abstract class EmailBaseEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "email", length = 100)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
