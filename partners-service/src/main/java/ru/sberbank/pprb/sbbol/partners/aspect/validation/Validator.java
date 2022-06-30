package ru.sberbank.pprb.sbbol.partners.aspect.validation;

import java.util.List;
import java.util.Map;

public interface Validator<T> {

    void validator(Map<String, List<String>> errors, T entity);
}
