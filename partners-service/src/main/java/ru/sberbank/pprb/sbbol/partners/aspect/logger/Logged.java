package ru.sberbank.pprb.sbbol.partners.aspect.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Добавление контекста в логгер для сквозной идентификации запроса
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Logged {

    /**
     * Выводить ли в логи параметры запроса и ответа
     *
     * @return true, если необходимо выводить в лог
     */
    boolean printRequestResponse() default false;

}
