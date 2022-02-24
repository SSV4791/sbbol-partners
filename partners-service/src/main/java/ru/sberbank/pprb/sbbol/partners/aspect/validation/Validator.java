package ru.sberbank.pprb.sbbol.partners.aspect.validation;

import java.util.List;

public interface Validator<T> {

    List<String> validation(T entity);
}
