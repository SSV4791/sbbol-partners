package ru.sberbank.pprb.sbbol.partners.aspect.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import ru.sberbank.pprb.sbbol.partners.service.audit.AuditService;

@Aspect
@Order(100)
public final class AuditAspect {
    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @SuppressWarnings("java:S1186")
    @Pointcut("@annotation(audit)")
    public void auditPointcut(Audit audit) {
    }

    @AfterReturning(pointcut = "auditPointcut(audit)", returning = "result", argNames = "joinPoint,audit,result")
    public void success(JoinPoint joinPoint, Audit audit, Object result) {
        sendEvent(audit.eventType().getSuccessEventName(), joinPoint.getArgs(), result);
    }

    @AfterThrowing(pointcut = "auditPointcut(audit)", argNames = "joinPoint,audit")
    public void error(JoinPoint joinPoint, Audit audit) {
        sendEvent(audit.eventType().getErrorEventName(), joinPoint.getArgs(), null);
    }

    private void sendEvent(String eventName, Object[] args, Object result) {
        if (result != null) {
            auditService.send(eventName, result);
        } else {
            auditService.send(eventName, args);
        }
    }
}
