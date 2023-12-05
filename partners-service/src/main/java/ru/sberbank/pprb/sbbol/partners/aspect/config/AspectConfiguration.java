package ru.sberbank.pprb.sbbol.partners.aspect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.sberbank.pprb.sbbol.partners.aspect.legacy.LegacyCheckAspect;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.LoggerAspect;
import ru.sberbank.pprb.sbbol.partners.aspect.validator.FraudValidatorAspect;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {

    @Bean
    FraudValidatorAspect fraudValidatorAspect(Validator validator) {
        return new FraudValidatorAspect(validator);
    }

    @Bean
    LoggerAspect loggedAspect() {
        return new LoggerAspect();
    }

    @Bean
    LegacyCheckAspect legacyCheckAspect(
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HttpServletRequest servletRequest,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        return new LegacyCheckAspect(servletRequest, legacySbbolAdapter);
    }
}
