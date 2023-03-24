package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit;

import java.util.Map;

@FunctionalInterface
public interface EventParamMapper<T> {
    Map<String, String> toEventParam(T value);
}
