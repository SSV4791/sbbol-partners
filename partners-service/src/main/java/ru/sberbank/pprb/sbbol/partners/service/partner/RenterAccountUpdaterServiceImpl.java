package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.List;

public class RenterAccountUpdaterServiceImpl implements RenterAccountUpdaterService{

    private final PartnerRepository partnerRepository;

    private final AccountRepository accountRepository;

    public RenterAccountUpdaterServiceImpl(PartnerRepository partnerRepository, AccountRepository accountRepository) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerEntity> getRenters(Pagination pagination) {
        return partnerRepository.findRenterByFilter(pagination);
    }

    @Override
    @Transactional
    public void updateAccountType(PartnerEntity renter) {
        var accounts = accountRepository.findByDigitalIdAndPartnerUuid(renter.getDigitalId(), renter.getUuid());
        accounts.stream()
            .filter(it -> PartnerType.PARTNER.equals(it.getPartnerType()))
            .forEach(it -> {
                it.setPartnerType(PartnerType.RENTER);
                accountRepository.save(it);
            });
    }
}
