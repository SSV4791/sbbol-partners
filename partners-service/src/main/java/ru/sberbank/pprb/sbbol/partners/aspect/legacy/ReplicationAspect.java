package ru.sberbank.pprb.sbbol.partners.aspect.legacy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BaseEntity;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Loggable
@Aspect
public class ReplicationAspect {

    private final AccountRepository accountRepository;

    private final ReplicationService replicationService;

    public ReplicationAspect(
        AccountRepository accountRepository,
        ReplicationService replicationService
    ) {
        this.accountRepository = accountRepository;
        this.replicationService = replicationService;
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.AccountService.saveAccount(..))")
    void callSaveAccount() {
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService.savePartner(..))")
    void callSavePartner() {
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.AccountService.updateAccount(..))")
    void callUpdatingAccount() {
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.AccountService.deleteAccounts(..))")
    void callDeleteAccounts() {
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService.deletePartners(..))")
    void callDeletePartners() {
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService.createAccountsSign(..))")
    void callCreatingSign() {
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService.deleteAccountsSign(..))")
    void callDeletingSign() {
    }

    @AfterReturning(value = "callSaveAccount()", returning = "account")
    void replicateForSaveAccount(Account account) {
        asyncCreateCounterparty(account);
    }

    @AfterReturning(value = "callSavePartner()", returning = "partnerCreateFullModelResponse")
    void replicationForSavePartner(PartnerCreateFullModelResponse partnerCreateFullModelResponse) {
        var accounts = partnerCreateFullModelResponse.getAccounts();
        Optional.ofNullable(accounts).ifPresent(this::asyncCreateCounterparty);
    }

    @AfterReturning(value = "callUpdatingAccount()", returning = "account")
    void replicateUpdatedAccount(Account account) {
        asyncUpdateCounterparty(account);
    }

    @AfterReturning(value = "callDeleteAccounts() && args(digitalId, accountIds)", argNames = "digitalId, accountIds")
    void replicateForDeleteAccounts(String digitalId, List<String> accountIds) {
        asyncDeleteCounterparty(digitalId, accountIds);
    }

    @Around(value = "callDeletePartners() && args(digitalId, partnersIds, fraudMetaData)", argNames = "joinPoint, digitalId, partnersIds, fraudMetaData")
    void replicateForDeletePartners(
        ProceedingJoinPoint joinPoint,
        String digitalId,
        List<String> partnersIds,
        FraudMetaData fraudMetaData
    ) throws Throwable {
        if (CollectionUtils.isEmpty(partnersIds)) {
            return;
        }
        List<String> accountIds = new ArrayList<>();
        for (var partnerId : partnersIds) {
            if (nonNull(partnerId)) {
                var partnerUuid = replicationService.toUUID(partnerId);
                var accounts = accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid);
                var ids = accounts.stream()
                    .map(BaseEntity::getUuid)
                    .map(UUID::toString)
                    .collect(Collectors.toList());
                accountIds.addAll(ids);
            }
        }
        joinPoint.proceed();
        if (!CollectionUtils.isEmpty(accountIds)) {
            asyncDeleteCounterparty(digitalId, accountIds);
        }
    }

    @AfterReturning(value = "callCreatingSign()", returning = "accountsSignInfoResponse")
    void replicateCreatedSign(AccountsSignInfoResponse accountsSignInfoResponse) {
        var digitalId = accountsSignInfoResponse.getDigitalId();
        var  accountsSignDetail = accountsSignInfoResponse.getAccountsSignDetail();
        for (var accountSignDetail : accountsSignDetail) {
            var accountId = accountSignDetail.getAccountId();
            asyncCreateSign(digitalId, replicationService.toUUID(accountId));
        }
    }

    @AfterReturning(value = "callDeletingSign() && args(digitalId, accountIds)", argNames = "digitalId, accountIds")
    void replicateDeletedSignSign(String digitalId, List<String> accountIds) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return;
        }
        accountIds.forEach(accountId -> asyncDeleteSign(digitalId, replicationService.toUUID(accountId)));
    }

    private void asyncCreateCounterparty(Account account) {
        CompletableFuture.runAsync(() -> replicationService.createCounterparty(account));
    }

    private void asyncCreateCounterparty(List<Account> accounts) {
        CompletableFuture.runAsync(() -> replicationService.createCounterparty(accounts));
    }

    private void asyncUpdateCounterparty(Account account) {
        CompletableFuture.runAsync(() -> replicationService.updateCounterparty(account));
    }

    private void asyncDeleteCounterparty(String digitalId, List<String> accountIds) {
        CompletableFuture.runAsync(() -> replicationService.deleteCounterparties(digitalId, accountIds));
    }

    private void asyncCreateSign(String digitalId, UUID accountUuid) {
        CompletableFuture.runAsync(() -> replicationService.saveSign(digitalId, accountUuid));
    }

    private void asyncDeleteSign(String digitalId, UUID accountUuid) {
        CompletableFuture.runAsync(() -> replicationService.deleteSign(digitalId, accountUuid));
    }
}
