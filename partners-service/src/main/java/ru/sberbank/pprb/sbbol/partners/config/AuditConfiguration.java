package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.AuditAspect;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerFullModelUpdateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerFullModelUpdateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.service.audit.AuditService;
import ru.sberbank.pprb.sbbol.partners.service.audit.AuditServiceImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgentRegistry;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountCreateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountCreateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountUpdateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountUpdateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountsDeleteErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountsDeleteSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AuditEventMapperAgentRegistryImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerCreateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerCreateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerDeleteErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerDeleteSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerFullModelCreateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerFullModelCreateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerUpdateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.PartnerUpdateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.SignAccountsCreateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.SignAccountsCreateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.SignAccountsDeleteErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.SignAccountsDeleteSuccessAuditMapperAgent;

import java.util.List;

@Configuration
@EnableAspectJAutoProxy
public class AuditConfiguration {
    @Bean
    AuditEventMapperAgent accountCreateSuccessAgent() {
        return new AccountCreateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent accountCreateErrorAgent() {
        return new AccountCreateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent accountUpdateSuccessAgent() {
        return new AccountUpdateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent accountUpdateErrorAgent() {
        return new AccountUpdateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent accountsDeleteSuccessAgent() {
        return new AccountsDeleteSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent accountsDeleteErrorAgent() {
        return new AccountsDeleteErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent signAccountsCreateSuccessAgent() {
        return new SignAccountsCreateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent signAccountsCreateErrorAgent() {
        return new SignAccountsCreateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent signAccountsDeleteSuccessAgent() {
        return new SignAccountsDeleteSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent signAccountsDeleteErrorAgent() {
        return new SignAccountsDeleteErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerFullModelCreateSuccessAgent() {
        return new PartnerFullModelCreateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerFullModelCreateErrorAgent() {
        return new PartnerFullModelCreateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerFullModelUpdateSuccessAgent() {
        return new PartnerFullModelUpdateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerFullModelUpdateErrorAgent() {
        return new PartnerFullModelUpdateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerCreateSuccessAgent() {
        return new PartnerCreateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerCreateErrorAgent() {
        return new PartnerCreateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerUpdateSuccessAgent() {
        return new PartnerUpdateSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerUpdateErrorAgent() {
        return new PartnerUpdateErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerDeleteSuccessAgent() {
        return new PartnerDeleteSuccessAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgent partnerDeleteErrorAgent() {
        return new PartnerDeleteErrorAuditMapperAgent();
    }

    @Bean
    AuditEventMapperAgentRegistry auditEventAgentRegistry(List<AuditEventMapperAgent> agents) {
        return new AuditEventMapperAgentRegistryImpl(agents);
    }

    @Bean
    AuditService auditService(AuditAdapter auditAdapter, AuditEventMapperAgentRegistry auditEventMapperAgentRegistry) {
        return new AuditServiceImpl(auditAdapter, auditEventMapperAgentRegistry);
    }

    @Bean
    AuditAspect auditAspect(AuditService auditService) {
        return new AuditAspect(auditService);
    }
}
