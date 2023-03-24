package ru.sberbank.pprb.sbbol.partners.aspect.audit;

import ru.sberbank.pprb.sbbol.partners.audit.model.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

    /**
     * EventType для
     * проводимой операции
     *
     * @return Тип события
     */
    EventType eventType();
}
