package ru.sberbank.pprb.sbbol.partners.partners.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.FraudModelValidationException;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HeaderFraudMetaDataConverter
    implements Converter<String, FraudMetaData> {

    private final ObjectMapper mapper;

    public HeaderFraudMetaDataConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Loggable
    @Override
    public FraudMetaData convert(String source) {
        var decodedBytes = Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
        try {
            return mapper.readValue(new String(decodedBytes), FraudMetaData.class);
        } catch (JsonProcessingException e) {
            throw new FraudModelValidationException(MessagesTranslator.toLocale("fraud.conversion_error_from_http_header"),e);
        }
    }
}
