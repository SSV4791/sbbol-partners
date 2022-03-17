package ru.sberbank.pprb.sbbol.partners.service.utils;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.exception.SignAccountException;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.MergeHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ReplicationHistoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PartnerUtils.class);

    private static final String PARTNER_NAME = "partner";
    private static final String ACCOUNT_NAME = "account";

    private final AccountRepository accountRepository;
    private final PartnerRepository partnerRepository;
    private final MergeHistoryRepository mergeHistoryRepository;
    private final ReplicationHistoryRepository replicationHistoryRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerMapper partnerMapper;
    private final CounterpartyMapper counterpartyMapper;
    private final AccountMapper accountMapper;

    public PartnerUtils(
        AccountRepository accountRepository,
        PartnerRepository partnerRepository,
        MergeHistoryRepository mergeHistoryRepository,
        ReplicationHistoryRepository replicationHistoryRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerMapper partnerMapper,
        CounterpartyMapper counterpartyMapper,
        AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.partnerRepository = partnerRepository;
        this.mergeHistoryRepository = mergeHistoryRepository;
        this.replicationHistoryRepository = replicationHistoryRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.partnerMapper = partnerMapper;
        this.counterpartyMapper = counterpartyMapper;
        this.accountMapper = accountMapper;
    }

    /**
     * Удаление партнёра
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор партнёра
     */
    public void deletePartnerByDigitalIdAndId(String digitalId, String id) {
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundPartner == null) {
            throw new EntryNotFoundException(PARTNER_NAME, digitalId, id);
        }
        partnerRepository.deleteById(foundPartner.getUuid());
        mergeHistoryRepository.deleteByMainUuid(foundPartner.getUuid());
    }

    /**
     * Обновление партнёра
     *
     * @param partner Партнёр
     * @return Обновленный партнёр
     */
    public Partner updatePartnerByPartner(Partner partner) {
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(partner.getDigitalId(), UUID.fromString(partner.getId()));
        if (foundPartner == null) {
            throw new EntryNotFoundException(PARTNER_NAME, partner.getDigitalId(), partner.getId());
        }
        partnerMapper.updatePartner(partner, foundPartner);
        PartnerEntity savePartner = partnerRepository.save(foundPartner);
        return partnerMapper.toPartner(savePartner);
    }

    /**
     * Обновление контрагента
     *
     * @param partner Партнёр
     * @return Обновлённый контрагент
     */
    public Partner updateCounterpartyByPartner(Partner partner) {
        Counterparty counterparty = legacySbbolAdapter.getByPprbGuid(partner.getDigitalId(), partner.getId());
        Counterparty updatedCounterparty = (Counterparty) SerializationUtils.clone(counterparty);
        counterpartyMapper.updateCounterparty(updatedCounterparty, partner);
        if (updatedCounterparty.getName() == null || updatedCounterparty.getAccount() == null ||
            updatedCounterparty.getBankBic() == null || updatedCounterparty.getTaxNumber() == null) {
            throw new ModelValidationException(Collections.singletonList("Сохранение контрагента в СББОЛ невозможно, одно из обязательных полей пусто" + updatedCounterparty));
        }
        if (counterparty.equals(updatedCounterparty)) {
            throw new ModelValidationException(Collections.singletonList("Обновление контрагента в СББОЛ невозможно, поля контрагента не отличаются" + updatedCounterparty));
        } else {
            Counterparty sbbolUpdatedCounterparty = legacySbbolAdapter.update(partner.getDigitalId(), updatedCounterparty);
            return counterpartyMapper.toPartner(sbbolUpdatedCounterparty, partner.getDigitalId());
        }
    }

    /**
     * Сохранение или обновление контрагента
     * !! Допущение: если контрагент не мигрирован, то мы можем обновить его только одним банком и одним корр счетом
     *
     * @param sbbolCounterparty Контрагент
     * @param account           Счёт
     * @return Обновлённый контрагент
     */
    public Counterparty createOrUpdateCounterparty(Counterparty sbbolCounterparty, AccountCreate account) {
        if (sbbolCounterparty == null) {
            return null;
        }
        BankCreate bank;
        BankAccountCreate bankAccount = null;
        if (CollectionUtils.isEmpty(account.getBanks()) || account.getBanks().size() != 1) {
            throw new ModelValidationException(Collections.singletonList("Сохранение контрагента в СББОЛ невозможно, банк должен быть заполнен единственным значением"));
        } else {
            bank = account.getBanks().get(0);
            if (!CollectionUtils.isEmpty(bank.getBankAccounts()) && bank.getBankAccounts().size() == 1) {
                bankAccount = bank.getBankAccounts().get(0);
            }
        }
        Counterparty updatedCounterparty = (Counterparty) SerializationUtils.clone(sbbolCounterparty);
        counterpartyMapper.updateCounterparty(updatedCounterparty, account, bank, bankAccount);
        if (updatedCounterparty.getName() == null || updatedCounterparty.getAccount() == null ||
            updatedCounterparty.getBankBic() == null || updatedCounterparty.getTaxNumber() == null) {
            throw new ModelValidationException(Collections.singletonList("Сохранение контрагента в СББОЛ невозможно, одно из обязательных полей пусто" + updatedCounterparty));
        }
        if (sbbolCounterparty.equals(updatedCounterparty)) {
            return null;
        }
        if (updatedCounterparty.getPprbGuid() == null) {
            return legacySbbolAdapter.create(account.getDigitalId(), updatedCounterparty);
        } else {
            return legacySbbolAdapter.update(account.getDigitalId(), updatedCounterparty);
        }
    }

    /**
     * Сохранение или обновление контрагента
     * !! Допущение: если контрагент не мигрирован, то мы можем обновить его только одним банком и одним корр счетом
     *
     * @param sbbolCounterparty Контрагент
     * @param account           Счёт
     * @return Обновлённый контрагент
     */
    public Counterparty createOrUpdateCounterparty(Counterparty sbbolCounterparty, AccountChange account) {
        if (sbbolCounterparty == null) {
            return null;
        }
        Bank bank;
        BankAccount bankAccount = null;
        if (CollectionUtils.isEmpty(account.getBanks()) || account.getBanks().size() != 1) {
            throw new ModelValidationException(Collections.singletonList("Сохранение контрагента в СББОЛ невозможно, банк должен быть заполнен единственным значением"));
        } else {
            bank = account.getBanks().get(0);
            if (!CollectionUtils.isEmpty(bank.getBankAccounts()) && bank.getBankAccounts().size() == 1) {
                bankAccount = bank.getBankAccounts().get(0);
            }
        }
        Counterparty updatedCounterparty = (Counterparty) SerializationUtils.clone(sbbolCounterparty);
        counterpartyMapper.updateCounterparty(updatedCounterparty, account, bank, bankAccount);
        if (updatedCounterparty.getName() == null || updatedCounterparty.getAccount() == null ||
            updatedCounterparty.getBankBic() == null || updatedCounterparty.getTaxNumber() == null) {
            throw new ModelValidationException(Collections.singletonList("Сохранение контрагента в СББОЛ невозможно, одно из обязательных полей пусто" + updatedCounterparty));
        }
        if (sbbolCounterparty.equals(updatedCounterparty)) {
            return null;
        }
        if (updatedCounterparty.getPprbGuid() == null) {
            return legacySbbolAdapter.create(account.getDigitalId(), updatedCounterparty);
        } else {
            return legacySbbolAdapter.update(account.getDigitalId(), updatedCounterparty);
        }
    }

    /**
     * Обновление счёта партёра
     *
     * @param account Счёт
     * @return Обновлённый счёт
     */
    public AccountEntity updatePartnerAccount(AccountChange account) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getId()));
        if (foundAccount == null) {
            throw new EntryNotFoundException(ACCOUNT_NAME, account.getDigitalId(), account.getId());
        }
        if (AccountStateType.SIGNED.equals(foundAccount.getState())) {
            throw new SignAccountException(Collections.singletonList("Ошибка обновления счёта клиента, нельзя обновлять подписанные счёта"));
        }
        accountMapper.updateAccount(account, foundAccount);
        return accountRepository.save(foundAccount);
    }

    /**
     * Удаление контрагента из СББОЛ и обновление replication_history
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор счёта
     * @param throwEx   Признак выкидывать Exception или нет при ошибке
     */
    public void deleteCounterpartyAndReplicationHistory(String digitalId, String id, boolean throwEx) {
        List<ReplicationHistoryEntity> replicationHistories = replicationHistoryRepository.findByAccountUuid(UUID.fromString(id));
        if (CollectionUtils.isEmpty(replicationHistories)) {
            return;
        }
        int historySize = replicationHistories.size();
        if (historySize > 1) {
            for (int i = 0; i < historySize - 1; i++) {
                try {
                    legacySbbolAdapter.delete(digitalId, replicationHistories.get(i).getSbbolGuid());
                    replicationHistoryRepository.delete(replicationHistories.get(i));
                } catch (Exception e) {
                    LOG.error("Возникла ошибка при удалении счёта в СББОЛ", e);
                    if (throwEx) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        ReplicationHistoryEntity history = replicationHistories.get(historySize - 1);
        try {
            legacySbbolAdapter.delete(digitalId, history.getSbbolGuid());
            history.setAccountUuid(null);
            history.setBankUuid(null);
            history.setBankAccountUuid(null);
            history.setSbbolGuid(null);
            replicationHistoryRepository.save(history);
        } catch (Exception e) {
            LOG.error("Возникла ошибка при удалении счёта в СББОЛ", e);
            if (throwEx) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Получение признака ЖКУ
     *
     * @param digitalId Идентификатор личного кабинета
     * @param inn       ИНН
     * @return признак принадлежит инн ЖКУ true - да, false - нет
     */
    public Boolean getGku(String digitalId, String inn) {
        var housingInn = legacySbbolAdapter.getHousingInn(digitalId, Set.of(inn));
        if (!CollectionUtils.isEmpty(housingInn)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
