package ru.sberbank.pprb.sbbol.partners.entity.partner.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class EmptyStringToNullConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String string) {
        return StringUtils.defaultIfEmpty(string, null);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return StringUtils.defaultIfEmpty(dbData, null);
    }
}
