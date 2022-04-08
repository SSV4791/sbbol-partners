package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessagesTranslator {

    private static ResourceBundleMessageSource messageSource;

    MessagesTranslator(ResourceBundleMessageSource messageSource) {
        MessagesTranslator.messageSource = messageSource;
    }

    public static String toLocale(String msgCode) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, null, locale);
    }

    public static String toLocale(String msgCode, String... agrs) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, agrs, locale);
    }
}
