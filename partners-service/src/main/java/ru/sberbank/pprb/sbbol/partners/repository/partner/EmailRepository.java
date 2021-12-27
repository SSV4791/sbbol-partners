package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;

import java.util.UUID;

@Repository
public interface EmailRepository extends CrudRepository<EmailEntity, UUID> {
}
