package ru.sberbank.pprb.sbbol.partners.aspect.validation;

import java.util.ArrayList;

public interface Validator<T> {

    ArrayList<String> validation(T entity);
}
