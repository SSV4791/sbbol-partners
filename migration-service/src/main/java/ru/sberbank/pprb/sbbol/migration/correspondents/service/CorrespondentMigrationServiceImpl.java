package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AutoJsonRpcServiceImpl
public class CorrespondentMigrationServiceImpl implements CorrespondentMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondentMigrationServiceImpl.class);

    private final MigrationPartnerMapper migrationPartnerMapper;
    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;

    public CorrespondentMigrationServiceImpl(
        MigrationPartnerMapper migrationPartnerMapper,
        PartnerRepository partnerRepository,
        AccountRepository accountRepository
    ) {
        this.migrationPartnerMapper = migrationPartnerMapper;
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public List<MigratedCorrespondentData> migrate(String digitalId, List<MigrationCorrespondentCandidate> correspondents) {
        var migratedCorrespondentData = new ArrayList<MigratedCorrespondentData>(correspondents.size());
        LOGGER.debug("Начало миграции контрагентов для организации c digitalId: {}. Количество кандидатов: {}", digitalId, correspondents.size());
        AccountEntity savedAccount;
        for (MigrationCorrespondentCandidate correspondent : correspondents) {
            try {
                var search = Stream.of(correspondent.getInn(), correspondent.getKpp(), correspondent.getName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(StringUtils.EMPTY));
                var searchPartner = partnerRepository.findByDigitalIdAndSearch(digitalId, search);
                if (searchPartner == null) {
                    var partnerEntity = migrationPartnerMapper.toPartnerEntity(digitalId, correspondent);
                    var save = partnerRepository.save(partnerEntity);
                    var accountEntity = migrationPartnerMapper.toAccountEntity(digitalId, save.getUuid(), correspondent);
                    savedAccount = accountRepository.save(accountEntity);
                } else {
                    var bic = correspondent.getBic();
                    var account = correspondent.getAccount();
                    var bankAccount = correspondent.getBankAccount();
                    var searchAccount = Stream.of(searchPartner.getUuid().toString(), account, bic, bankAccount)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(StringUtils.EMPTY));
                    var foundAccount =
                        accountRepository.findByDigitalIdAndSearch(digitalId, searchAccount);
                    if (foundAccount == null) {
                        var accountEntity =
                            migrationPartnerMapper.toAccountEntity(digitalId, searchPartner.getUuid(), correspondent);
                        savedAccount = accountRepository.save(accountEntity);
                    } else {
                        migrationPartnerMapper.updateAccountEntity(digitalId, searchPartner.getUuid(), correspondent, foundAccount);
                        savedAccount = accountRepository.save(foundAccount);
                    }
                    if (correspondent.isSigned()) {
                        migrationPartnerMapper.updatePartnerEntity(digitalId, correspondent, searchPartner);
                    }
                    partnerRepository.save(searchPartner);
                }
            } catch (Exception ex) {
                LOGGER.error(
                    "В процессе миграции контрагента с sbbolReplicationGuid: {}, произошла ошибка. Причина: {}",
                    correspondent.getReplicationGuid(),
                    ex.getCause()
                );
                throw ex;
            }
            String pprbGuid = null;
            if (savedAccount.getUuid() != null) {
                pprbGuid = savedAccount.getUuid().toString();
            }
            migratedCorrespondentData.add(
                new MigratedCorrespondentData(
                    pprbGuid,
                    correspondent.getReplicationGuid(),
                    savedAccount.getVersion()
                )
            );
        }
        LOGGER.debug("Для организации c digitalId: {}, мигрировано {} контрагентов", digitalId, migratedCorrespondentData.size());
        return migratedCorrespondentData;
    }
}
