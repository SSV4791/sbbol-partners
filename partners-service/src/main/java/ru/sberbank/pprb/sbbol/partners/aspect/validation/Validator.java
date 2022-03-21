package ru.sberbank.pprb.sbbol.partners.aspect.validation;

import java.util.List;

public interface Validator<T> {

    void validation(List<String> errors, T entity);
}
