package ru.sberbank.pprb.sbbol.partners.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormResolver;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;
import ru.sberbank.pprb.sbbol.partners.service.legalform.PartnerNameResolver;

@Configuration
public class ServiceConfiguration {

    @Bean
    LegalFormInspector legalFormInspector(
        LegalFormResolver legalFormResolver,
        PartnerNameResolver partnerNameResolver
    ) {
        return new LegalFormInspector(
            legalFormResolver,
            partnerNameResolver);
    }

    @Bean
    LegalFormResolver legalFormDetermination() {
        return new LegalFormResolver();
    }

    @Bean
    PartnerNameResolver nameDetermination() {
        return new PartnerNameResolver();
    }
}
