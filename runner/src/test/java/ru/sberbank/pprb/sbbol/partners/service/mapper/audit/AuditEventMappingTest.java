package ru.sberbank.pprb.sbbol.partners.service.mapper.audit;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountCreateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountCreateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountUpdateErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountUpdateSuccessAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountsDeleteErrorAuditMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl.AccountsDeleteSuccessAuditMapperAgent;
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
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AuditEventMappingTest extends BaseUnitConfiguration {

    @Test
    void accountCreateErrorAgentMappingTest() {
        var accountCreate =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(AccountCreate.class));

        var params =
            Allure.step("Получение параметров события", () -> {
                var args = new Object[]{accountCreate};
                var agent = new AccountCreateErrorAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(args);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    accountCreate.getPartnerId().toString(),
                    accountCreate.getDigitalId(),
                    accountCreate.getAccount(),
                    accountCreate.getComment(),
                    accountCreate.getBank().toString()
                );
        });
    }

    @ParameterizedTest
    @MethodSource
    void accountCreateAndUpdateSuccessAgentMappingTest(AuditEventMapperAgent agent) {
        var accountCreate =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(Account.class));
        var params =
            Allure.step("Получение параметров события", () -> agent.getAuditEventMapper().toEventParam(accountCreate));

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    accountCreate.getId().toString(),
                    accountCreate.getPartnerId().toString(),
                    accountCreate.getDigitalId(),
                    accountCreate.getVersion().toString(),
                    accountCreate.getBudget().toString(),
                    accountCreate.getAccount(),
                    accountCreate.getPriorityAccount().toString(),
                    accountCreate.getBank().toString(),
                    accountCreate.getState().toString(),
                    accountCreate.getComment()
                );
        });
    }

    static Stream<AuditEventMapperAgent> accountCreateAndUpdateSuccessAgentMappingTest() {
        return Stream.of(
            new AccountCreateSuccessAuditMapperAgent(),
            new AccountUpdateSuccessAuditMapperAgent()
        );
    }

    @ParameterizedTest
    @MethodSource
    void deleteAgentsMappingTest(AuditEventMapperAgent agent) {
        var digitalId =
            Allure.step("Создание digitalId", () -> factory.manufacturePojo(String.class));
        var ids =
            Allure.step("Создание списка id", () -> factory.manufacturePojo(ArrayList.class, String.class));
        var args =
            Allure.step("Создание объекта для маппинга", () ->
                new Object[]{digitalId, ids});
        var params =
            Allure.step("Получение параметров события", () -> agent.getAuditEventMapper().toEventParam(args));

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    digitalId,
                    ids.toString()
                );
        });
    }

    static Stream<AuditEventMapperAgent> deleteAgentsMappingTest() {
        return Stream.of(
            new AccountsDeleteErrorAuditMapperAgent(),
            new AccountsDeleteSuccessAuditMapperAgent(),
            new PartnerDeleteErrorAuditMapperAgent(),
            new PartnerDeleteSuccessAuditMapperAgent(),
            new SignAccountsDeleteErrorAuditMapperAgent(),
            new SignAccountsDeleteSuccessAuditMapperAgent()
        );
    }

    @Test
    void accountUpdateErrorAgentMappingTest() {
        var accountChange =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(AccountChange.class));
        var params =
            Allure.step("Получение параметров события", () -> {
                var args = new Object[]{accountChange};
                var agent = new AccountUpdateErrorAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(args);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    accountChange.getPartnerId().toString(),
                    accountChange.getDigitalId(),
                    accountChange.getAccount(),
                    accountChange.getComment(),
                    accountChange.getBank().toString()
                );
        });
    }

    @Test
    void partnerCreateErrorAgentMappingTest() {
        var partner =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(PartnerCreate.class));

        var params =
            Allure.step("Получение параметров события", () -> {
                var args = new Object[]{partner};
                var agent = new PartnerCreateErrorAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(args);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    partner.getDigitalId(),
                    partner.getLegalForm().toString(),
                    partner.getOrgName(),
                    partner.getFirstName(),
                    partner.getSecondName(),
                    partner.getMiddleName(),
                    partner.getInn(),
                    partner.getKpp(),
                    partner.getOgrn(),
                    partner.getOkpo(),
                    partner.getPhones().toString(),
                    partner.getEmails().toString(),
                    partner.getComment(),
                    partner.getCitizenship().toString()
                );
        });
    }

    @ParameterizedTest
    @MethodSource
    void partnerCreateAndUpdateSuccessAgentMappingTest(AuditEventMapperAgent agent) {
        var partner =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(Partner.class));
        var params =
            Allure.step("Получение параметров события", () -> agent.getAuditEventMapper().toEventParam(partner));

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    partner.getId().toString(),
                    partner.getDigitalId(),
                    partner.getVersion().toString(),
                    partner.getLegalForm().toString(),
                    partner.getOrgName(),
                    partner.getFirstName(),
                    partner.getSecondName(),
                    partner.getMiddleName(),
                    partner.getInn(),
                    partner.getKpp(),
                    partner.getOgrn(),
                    partner.getOkpo(),
                    partner.getEmails().toString(),
                    partner.getPhones().toString(),
                    partner.getComment(),
                    partner.getGku().toString(),
                    partner.getCitizenship().toString()
                );
        });
    }

    static Stream<AuditEventMapperAgent> partnerCreateAndUpdateSuccessAgentMappingTest() {
        return Stream.of(
            new PartnerCreateSuccessAuditMapperAgent(),
            new PartnerUpdateSuccessAuditMapperAgent()
        );
    }

    @Test
    void partnerFullModelCreateErrorAgentMappingTest() {
        var partner =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(PartnerCreateFullModel.class));
        var params =
            Allure.step("Получение параметров события", () -> {
                var args = new Object[]{partner};
                var agent = new PartnerFullModelCreateErrorAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(args);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    partner.getDigitalId(),
                    partner.getLegalForm().toString(),
                    partner.getOrgName(),
                    partner.getFirstName(),
                    partner.getSecondName(),
                    partner.getMiddleName(),
                    partner.getInn(),
                    partner.getKpp(),
                    partner.getOgrn(),
                    partner.getOkpo(),
                    partner.getComment(),
                    partner.getCitizenship().toString(),
                    partner.getAccounts().toString(),
                    partner.getEmails().toString(),
                    partner.getPhones().toString(),
                    partner.getContacts().toString(),
                    partner.getAddress().toString(),
                    partner.getDocuments().toString()
                );
        });
    }

    @Test
    void partnerFullModelCreateSuccessAgentMappingTest() {
        var partner =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(PartnerFullModelResponse.class));
        var params =
            Allure.step("Получение параметров события", () -> {
                var agent = new PartnerFullModelCreateSuccessAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(partner);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    partner.getId().toString(),
                    partner.getDigitalId(),
                    partner.getLegalForm().toString(),
                    partner.getVersion().toString(),
                    partner.getOrgName(),
                    partner.getFirstName(),
                    partner.getSecondName(),
                    partner.getMiddleName(),
                    partner.getInn(),
                    partner.getKpp(),
                    partner.getOgrn(),
                    partner.getOkpo(),
                    partner.getComment(),
                    partner.getCitizenship().toString(),
                    partner.getAccounts().toString(),
                    partner.getEmails().toString(),
                    partner.getPhones().toString(),
                    partner.getContacts().toString(),
                    partner.getAddress().toString(),
                    partner.getGku().toString(),
                    partner.getDocuments().toString()
                );
        });
    }

    @Test
    void partnerUpdateErrorAgentMappingTest() {
        var partner =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(Partner.class));

        var params =
            Allure.step("Получение параметров события", () -> {
                var args = new Object[]{partner};
                var agent = new PartnerUpdateErrorAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(args);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    partner.getId().toString(),
                    partner.getDigitalId(),
                    partner.getVersion().toString(),
                    partner.getLegalForm().toString(),
                    partner.getOrgName(),
                    partner.getFirstName(),
                    partner.getSecondName(),
                    partner.getMiddleName(),
                    partner.getInn(),
                    partner.getKpp(),
                    partner.getOgrn(),
                    partner.getOkpo(),
                    partner.getEmails().toString(),
                    partner.getPhones().toString(),
                    partner.getComment(),
                    partner.getGku().toString(),
                    partner.getCitizenship().toString()
                );
        });
    }

    @Test
    void signAccountsCreateErrorAgentMappingTest() {
        var accountsSignInfo =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(AccountsSignInfo.class));

        var params =
            Allure.step("Получение параметров события", () -> {
                var args = new Object[]{accountsSignInfo};
                var agent = new SignAccountsCreateErrorAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(args);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    accountsSignInfo.getDigitalId(),
                    accountsSignInfo.getDigitalUserId(),
                    accountsSignInfo.getAccountsSignDetail().toString()
                );
        });
    }

    @Test
    void signAccountsCreateSuccessAgentMappingTest() {
        var accountsSignInfoResponse =
            Allure.step("Создание объекта для маппинга", () -> factory.manufacturePojo(AccountsSignInfoResponse.class));
        var params =
            Allure.step("Получение параметров события", () -> {
                var agent = new SignAccountsCreateSuccessAuditMapperAgent();
                return agent.getAuditEventMapper().toEventParam(accountsSignInfoResponse);
            });

        Allure.step("Проверка результата", () -> {
            assertThat(params)
                .isNotNull()
                .isNotEmpty()
                .containsValues(
                    accountsSignInfoResponse.getDigitalId(),
                    accountsSignInfoResponse.getAccountsSignDetail().toString()
                );
        });
    }
}
