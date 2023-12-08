package ru.sberbank.pprb.sbbol.partners.mapper.counterparty.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import java.util.List;
import java.util.UUID;

public abstract class CounterpartyMapperDecorator implements CounterpartyMapper {

    @Autowired
    @Qualifier("delegate")
    private CounterpartyMapper delegate;

    @Autowired
    private IdsHistoryService idsHistoryService;

    @Override
    public Counterparty toCounterparty(PartnerEntity partner, AccountEntity account) {
        Counterparty counterparty = delegate.toCounterparty(partner, account);
        List<UUID> history = idsHistoryService.getAccountByPprbUuid(account.getDigitalId(), account.getUuid());
        counterparty.setReplicationGuid(toReplicationGuid(history));
        return counterparty;
    }

    static UUID toReplicationGuid(List<UUID> idLinks) {
        if (CollectionUtils.isEmpty(idLinks)) {
            return null;
        }
        if (idLinks.size() > 1) {
            throw new IllegalStateException("При репликации возникла ошибка. Найдено несколько UUID для репликации");
        }
        return idLinks.get(0);
    }
}
