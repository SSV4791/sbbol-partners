package ru.sberbank.pprb.sbbol.partners.aspect.logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.core.annotation.AnnotationUtils;

import static java.util.Objects.nonNull;

@Aspect
public final class LoggerAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerAspect.class);

    @SuppressWarnings("java:S1186")
    @Pointcut(
        "@target(ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable) || " +
        "@annotation(ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable)")
    public void loggable() {
    }

    @Around(
        "(execution(* ru.sberbank.pprb.sbbol.partners..*(..)) && loggable()) || " +
            "execution(* ru.sberbank.pprb.sbbol.partners.mapper..*Mapper*.*(..))"
    )
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        var signature = joinPoint.getSignature();
        var method = ((MethodSignature) signature).getMethod();
        var args = joinPoint.getArgs();
        var loggableClassAnnotation = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), Loggable.class);
        var isClassLogged = nonNull(loggableClassAnnotation) && loggableClassAnnotation.value();
        var loggableMethodAnnotation = AnnotationUtils.findAnnotation(method, Loggable.class);
        var isLogged = nonNull(loggableMethodAnnotation) ? loggableMethodAnnotation.value() : isClassLogged;
        Level level = null;
        if (isLogged) {
            level = nonNull(loggableMethodAnnotation) ? loggableMethodAnnotation.level() : loggableClassAnnotation.level();
        }
        logInvocation(signature, args, isLogged, level);
        try {
            Object result = joinPoint.proceed();
            logResult(signature, result, isLogged, level);
            return result;
        } catch (Throwable ex) {
            logException(signature, ex, isLogged, level);
            throw ex;
        }
    }

    private void logInvocation(Signature signature, Object[] args, boolean isLogged, Level level) {
        var messagePattern = "Вызов метода {} с параметрами {}";
        if (isLogged) {
            switch (level) {
                case TRACE -> LOGGER.trace(messagePattern, signature, args);
                case DEBUG -> LOGGER.debug(messagePattern, signature, args);
                case INFO -> LOGGER.info(messagePattern, signature, args);
                case WARN -> LOGGER.warn(messagePattern, signature, args);
                case ERROR -> LOGGER.error(messagePattern, signature, args);
            }
        }
    }

    private void logResult(Signature signature, Object arg, boolean isLogged, Level level) {
        var messagePattern = "Метод {} результат вызова {}";
        if (isLogged) {
            switch (level) {
                case TRACE -> LOGGER.trace(messagePattern, signature, arg);
                case DEBUG -> LOGGER.debug(messagePattern, signature, arg);
                case INFO -> LOGGER.info(messagePattern, signature, arg);
                case WARN -> LOGGER.warn(messagePattern, signature, arg);
                case ERROR -> LOGGER.error(messagePattern, signature, arg);
            }
        }
    }

    private void logException(Signature signature, Throwable ex, boolean isLogged, Level level) {
        var messagePattern = "Метод {} Класс исключения {} Сообщение об ошибке {} StackTrace {}";
        if (isLogged) {
            var exceptionClassName = ex.getClass().getName();
            var exceptionMessage = ex.getMessage();
            var stackTrace = ExceptionUtils.getStackTrace(ex);
            switch (level) {
                case TRACE -> LOGGER.trace(messagePattern, signature, exceptionClassName, exceptionMessage, stackTrace);
                case DEBUG -> LOGGER.debug(messagePattern, signature, exceptionClassName, exceptionMessage, stackTrace);
                case INFO -> LOGGER.info(messagePattern, signature, exceptionClassName, exceptionMessage, stackTrace);
                case WARN -> LOGGER.warn(messagePattern, signature, exceptionClassName, exceptionMessage, stackTrace);
                case ERROR -> LOGGER.error(messagePattern, signature, exceptionClassName, exceptionMessage, stackTrace);
            }
        }
    }
}
