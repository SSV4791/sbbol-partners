package ru.sberbank.pprb.sbbol.partners.aspect.logger;

import org.slf4j.event.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Loggable {
    boolean value() default true;
    Level level() default Level.TRACE;
}
