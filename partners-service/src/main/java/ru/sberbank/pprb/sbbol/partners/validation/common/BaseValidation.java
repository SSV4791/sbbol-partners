package ru.sberbank.pprb.sbbol.partners.validation.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BaseValidation {

    private BaseValidation() {
        throw new AssertionError();
    }

    public static void setError(Map<String, List<String>> errors, String field, String message) {
        if (errors.containsKey(field)) {
            errors.get(field).add(message);
        } else {
            var messageList = new ArrayList<String>();
            messageList.add(message);
            errors.put(field, messageList);
        }
    }
}
