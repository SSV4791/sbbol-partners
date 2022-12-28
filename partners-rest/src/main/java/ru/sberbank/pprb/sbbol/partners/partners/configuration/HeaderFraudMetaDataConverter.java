package ru.sberbank.pprb.sbbol.partners.partners.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.FraudModelValidationException;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

public class HeaderFraudMetaDataConverter
    implements Converter<String, FraudMetaData> {

    private final ObjectMapper mapper;

    public HeaderFraudMetaDataConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FraudMetaData convert(String source) {
        try {
            return mapper.readValue(source, FraudMetaData.class);
        } catch (JsonProcessingException e) {
            throw new FraudModelValidationException(MessagesTranslator.toLocale("fraud.conversion_error_from_http_header"),e);
        }
    }
}
