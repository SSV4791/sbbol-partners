package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerAccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccount;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountsResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerAdapterRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Service
public class PartnerAccountServiceImpl implements PartnerAccountService {

    private final PartnerRepository partnerRepository;
    private final PartnerAdapterRepository partnerAdapterRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerAccountMapper partnerAccountMapper;

    public PartnerAccountServiceImpl(
        PartnerRepository partnerRepository,
        PartnerAdapterRepository partnerAdapterRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerAccountMapper partnerAccountMapper) {
        this.partnerRepository = partnerRepository;
        this.partnerAdapterRepository = partnerAdapterRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.partnerAccountMapper = partnerAccountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerAccountResponse getAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            AccountEntity account = partnerAdapterRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
            PartnerAccount response = partnerAccountMapper.toAccount(account);
            return new PartnerAccountResponse().account(response);
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerAccountsResponse getAccounts(PartnerAccountsFilter accountsFilter) {
        if (legacySbbolAdapter.checkMigration(accountsFilter.getDigitalId())) {
            Slice<AccountEntity> response;
            if (accountsFilter.getPagination() == null) {
                response = partnerAdapterRepository.findAllByDigitalId(accountsFilter.getDigitalId(), Sort.by("digitalId"));
            } else {
                Pagination pagination = accountsFilter.getPagination();
                response = partnerAdapterRepository.findAllByDigitalId(accountsFilter.getDigitalId(), PageRequest.of(pagination.getOffset(), pagination.getCount(), Sort.by("digitalId")));
            }
            PartnerAccountsResponse accountResponse = new PartnerAccountsResponse();
            for (AccountEntity entity : response) {
                accountResponse.addAccountsItem(partnerAccountMapper.toAccount(entity));
            }
            accountResponse.setPagination(
                new Pagination()
                    .offset(response.getNumber())
                    .count(response.getSize())
                    .hasNextPage(response.isLast())
            );
            return accountResponse;
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerAccountResponse saveAccount(PartnerAccount account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            PartnerEntity partner = partnerRepository.getByIdAndDeletedIsFalse(UUID.fromString(account.getPartnerUuid()));
            if (partner != null) {
                var requestAccount = partnerAccountMapper.toAccount(account);
                requestAccount.setPartner(partner);
                fillEntity(requestAccount);
                var saveAccount = partnerAdapterRepository.save(requestAccount);
                var response = partnerAccountMapper.toAccount(saveAccount);
                return new PartnerAccountResponse().account(response);
            } else {
                //TODO добавить обработку ошибок
                return null;
            }
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerAccountResponse updateAccount(PartnerAccount account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            AccountEntity searchAccount = partnerAdapterRepository.getByDigitalIdAndId(account.getDigitalId(), UUID.fromString(account.getUuid()));
            partnerAccountMapper.updateAccount(account, searchAccount);
            fillEntity(searchAccount);
            AccountEntity saveAccount = partnerAdapterRepository.save(searchAccount);
            var response = partnerAccountMapper.toAccount(saveAccount);
            return new PartnerAccountResponse().account(response);
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerAccountResponse deleteAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            AccountEntity searchAccount = partnerAdapterRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
            if (searchAccount != null) {
                partnerAdapterRepository.delete(searchAccount);
            }
            var response = partnerAccountMapper.toAccount(searchAccount);
            return new PartnerAccountResponse().account(response);
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    //TODO надо перенести на маппер часть логики
    private void fillEntity(AccountEntity account) {
        if (account.getBank() != null) {
            account.getBank().setAccount(account);
            for (var bankAccount : account.getBank().getBankAccounts()) {
                bankAccount.setBank(account.getBank());
            }
        }
    }
}
