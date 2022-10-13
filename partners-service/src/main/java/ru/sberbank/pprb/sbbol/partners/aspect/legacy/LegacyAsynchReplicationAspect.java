package ru.sberbank.pprb.sbbol.partners.aspect.legacy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import ru.sberbank.pprb.sbbol.partners.exception.AsynchReplicationException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.service.replication.AsynchReplicationService;

import static java.lang.String.format;

@Aspect
public class LegacyAsynchReplicationAspect {

    private static final int DIGITAL_ID_INDEX = 0;
    private static final int COUNTERPARTY_INDEX = 1;
    private final AsynchReplicationService asynchReplicationService;

    public LegacyAsynchReplicationAspect(AsynchReplicationService asynchReplicationService) {
        this.asynchReplicationService = asynchReplicationService;
    }

    @SuppressWarnings("java:S1186")
    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter*.create(..))")
    void callCreatingCounterparty() {
    }

    @SuppressWarnings("java:S1186")
    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter*.update(..))")
    void callUpdatingCounterparty() {
    }

    @SuppressWarnings("java:S1186")
    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter*.delete(..))")
    void callDeletingCounterparty() {
    }

    @SuppressWarnings("java:S1186")
    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter*.saveSign(..))")
    void callCreatingSign() {
    }

    @SuppressWarnings("java:S1186")
    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter*.removeSign(..))")
    void callDeletingSign() {
    }

    @Around(value = "callCreatingCounterparty()")
    Counterparty createCounterparty(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var args = proceedingJoinPoint.getArgs();
        var digitalId = (String) args[DIGITAL_ID_INDEX];
        var counterparty = (Counterparty) args[COUNTERPARTY_INDEX];
        try {
            return (Counterparty) proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            if (!asynchReplicationService.isEnable()) {
                throw e;
            }
            try {
                asynchReplicationService.createCounterparty(digitalId, counterparty);
            } catch (RuntimeException ex) {
                throw new AsynchReplicationException(
                    format("Ошибка асинхронной репликации при создании контрагента: digitalId = [%s], counterparty = [%s]",
                        digitalId, counterparty), ex);
            }
        }
        return counterparty;
    }

    @Around(value = "callUpdatingCounterparty()")
    Counterparty updateCounterparty(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var args = proceedingJoinPoint.getArgs();
        var digitalId = (String) args[DIGITAL_ID_INDEX];
        var counterparty = (Counterparty) args[COUNTERPARTY_INDEX];
        try {
            return (Counterparty) proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            if (!asynchReplicationService.isEnable()) {
                throw e;
            }
            try {
                asynchReplicationService.updateCounterparty(digitalId, counterparty);
            } catch (RuntimeException ex) {
                throw new AsynchReplicationException(
                    format("Ошибка асинхронной репликации при обновлении контрагента: digitalId = [%s], counterparty = [%s]",
                        digitalId, counterparty), ex);
            }
        }
        return counterparty;
    }

    @Around(value = "callDeletingCounterparty()")
    void deleteCounterparty(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var args = proceedingJoinPoint.getArgs();
        var digitalId = (String) args[DIGITAL_ID_INDEX];
        var counterpartyId = (String) args[COUNTERPARTY_INDEX];
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            if (!asynchReplicationService.isEnable()) {
                throw e;
            }
            try {
                asynchReplicationService.deleteCounterparty(digitalId, counterpartyId);
            } catch (RuntimeException ex) {
                throw new AsynchReplicationException(
                    format("Ошибка асинхронной репликации при удалении контрагента: digitalId = [%s], counterpartyId = [%s]",
                        digitalId, counterpartyId), ex);
            }
        }
    }

    @Around(value = "callCreatingSign()")
    void createSign(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var args = proceedingJoinPoint.getArgs();
        var digitalId = (String) args[DIGITAL_ID_INDEX];
        var counterpartySignData = (CounterpartySignData) args[COUNTERPARTY_INDEX];
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            if (!asynchReplicationService.isEnable()) {
                throw e;
            }
            try {
                asynchReplicationService.createSign(digitalId, counterpartySignData);
            } catch (RuntimeException ex) {
                throw new AsynchReplicationException(
                    format("Ошибка асинхронной репликации при создании подписи: digitalId = [%s], counterpartySignData = [%s]",
                        digitalId, counterpartySignData), ex);
            }
        }
    }

    @Around(value = "callDeletingSign()")
    void deleteSign(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var args = proceedingJoinPoint.getArgs();
        var digitalId = (String) args[DIGITAL_ID_INDEX];
        var counterpartyId = (String) args[COUNTERPARTY_INDEX];
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            if (!asynchReplicationService.isEnable()) {
                throw e;
            }
            try {
                asynchReplicationService.deleteSign(digitalId, counterpartyId);
            } catch (RuntimeException ex) {
                throw new AsynchReplicationException(
                    format("Ошибка асинхронной репликации при удалении подписи: digitalId = [%s], counterpartyId = [%s]",
                        digitalId, counterpartyId), ex);
            }
        }
    }
}
