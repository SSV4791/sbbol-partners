package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.List;
import java.util.Map;

public class OptimisticLockException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:OPTIMISTIC_LOCK_EXCEPTION";

    private final String text;

    private final Map<String, List<String>> errors;

    public OptimisticLockException(Long foundVersion, Long version) {
        super(MessagesTranslator.toLocale("error.message.check.validation"));
        text = EXCEPTION;
        errors = Map.of("version", List.of(
            MessagesTranslator.toLocale("default.fields.object_version_error", foundVersion, version)
        ));
    }

    public String getText() {
        return text;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
