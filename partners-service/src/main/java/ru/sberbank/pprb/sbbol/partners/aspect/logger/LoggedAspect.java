package ru.sberbank.pprb.sbbol.partners.aspect.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * Аспект, обрабатывающий вызов методов с логированием, отмеченных аннотацией {@link Logged}
 */
@Aspect
@Component
public final class LoggedAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedAspect.class);

    /**
     * Такое название используется в платформенном логгере для заполнения поля "Запрос" и поиска по нему
     */
    private static final String REQUEST_UID = "requestUid";

    @Around("within(ru.sberbank.pprb.sbbol.partners..*) && @annotation(logged)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, Logged logged) throws Throwable {
        String uid = MDC.get(REQUEST_UID);
        boolean existingUid = uid != null;
        if (!existingUid) {
            MDC.put(REQUEST_UID, UUID.randomUUID().toString());
        }
        try {
            logInvocation(logged, joinPoint.getSignature(), joinPoint.getArgs());
            Object result = joinPoint.proceed();
            logResult(logged, joinPoint.getSignature(), result);
            return result;
        } finally {
            if (!existingUid) {
                MDC.remove(REQUEST_UID);
            }
        }
    }

    private void logInvocation(Logged logged, Signature signature, Object[] args) {
        if (logged.printRequestResponse() && args.length != 0) {
            LOGGER.debug("Вызов метода {} с параметрами {}", signature, new LazyArrayPrinter(args));
        }
    }

    private void logResult(Logged logged, Signature signature, Object arg) {
        if (logged.printRequestResponse()) {
            LOGGER.debug("Метод {} результат вызова {}", signature, arg);
        }
    }

    /**
     * Обертка над массивом для отложенного вызова метода {@link Arrays#toString}
     */
    private static final class LazyArrayPrinter {
        private final Object[] args;

        private LazyArrayPrinter(Object[] args) {
            this.args = args;
        }

        @Override
        public String toString() {
            return Arrays.toString(args);
        }
    }

}
