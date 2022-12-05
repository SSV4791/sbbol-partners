package ru.sberbank.pprb.sbbol.partners.validator;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.accounts.AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator;

import javax.validation.ConstraintValidatorContext;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class AccountBalanceValidationTest extends BaseUnitConfiguration {

    @MockBean
    private PartnerService partnerService;
    @Mock
    private ConstraintValidatorContext context;

    @Test
    void accountAttributeBalancePhysicalPerson_accountChangeDto_physicalPerson() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .legalForm(LegalForm.PHYSICAL_PERSON);
        when(partnerService.getPartner(any(), any()))
            .thenReturn(partner);
        var account = factory.manufacturePojo(AccountChange.class)
            .account("03202643982374972392")
            .bank(new Bank()
                .bic("044525225")
                .bankAccount(new BankAccount()
                    .bankAccount("40102000000000000000")));
        var validator =
            spy(new AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(account, context));
    }

    @Test
    void accountAttributeBalancePhysicalPerson_accountChangeDto_entrepreneur() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .legalForm(LegalForm.ENTREPRENEUR);
        when(partnerService.getPartner(any(), any()))
            .thenReturn(partner);
        var account = factory.manufacturePojo(AccountChange.class)
            .account("03202643982374972392")
            .bank(new Bank()
                .bic("044525225")
                .bankAccount(new BankAccount()
                    .bankAccount("40102000000000000000")));
        var validator =
            spy(new AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(account, context));
    }

    @Test
    void accountAttributeBalancePhysicalPerson_accountChangeDto_legalEntity() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .legalForm(LegalForm.LEGAL_ENTITY);
        when(partnerService.getPartner(any(), any()))
            .thenReturn(partner);
        var account = factory.manufacturePojo(AccountChange.class)
            .account("03202643982374972392")
            .bank(new Bank()
                .bic("044525225")
                .bankAccount(new BankAccount()
                    .bankAccount("40102000000000000000")));
        var validator =
            spy(new AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertTrue(validator.isValid(account, context));
    }

    @Test
    void accountAttributeBalancePhysicalPerson_accountCreateDto_physicalPerson() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .legalForm(LegalForm.PHYSICAL_PERSON);
        when(partnerService.getPartner(any(), any()))
            .thenReturn(partner);
        var account = factory.manufacturePojo(AccountCreate.class)
            .account("03202643982374972392")
            .bank(new BankCreate()
                .bic("044525225")
                .bankAccount(new BankAccountCreate()
                    .bankAccount("40102000000000000000")));
        var validator =
            spy(new AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(account, context));
    }

    @Test
    void accountAttributeBalancePhysicalPerson_accountCreateDto_entrepreneur() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .legalForm(LegalForm.ENTREPRENEUR);
        when(partnerService.getPartner(any(), any()))
            .thenReturn(partner);
        var account = factory.manufacturePojo(AccountCreate.class)
            .account("03202643982374972392")
            .bank(new BankCreate()
                .bic("044525225")
                .bankAccount(new BankAccountCreate()
                    .bankAccount("40102000000000000000")));
        var validator =
            spy(new AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(account, context));
    }

    @Test
    void accountAttributeBalancePhysicalPerson_accountCreateDto_legalEntity() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .legalForm(LegalForm.LEGAL_ENTITY);
        when(partnerService.getPartner(any(), any()))
            .thenReturn(partner);
        var account = factory.manufacturePojo(AccountCreate.class)
            .account("03202643982374972392")
            .bank(new BankCreate()
                .bic("044525225")
                .bankAccount(new BankAccountCreate()
                    .bankAccount("40102000000000000000")));
        var validator =
            spy(new AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertTrue(validator.isValid(account, context));
    }

    @Test
    void accountsAttributeBalancePhysicalPerson_partnerCreateFullModelDto_physicalPerson() {
        var partner = factory.manufacturePojo(PartnerCreateFullModel.class)
            .legalForm(LegalForm.PHYSICAL_PERSON)
            .accounts(
                Set.of(
                    new AccountCreateFullModel()
                        .account("03202643982374972392")
                        .bank(new BankCreate()
                            .bic("044525225")
                            .bankAccount(new BankAccountCreate()
                                .bankAccount("40102000000000000000")))
                )
            );
        var validator =
            spy(new AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void accountsAttributeBalancePhysicalPerson_partnerCreateFullModelDto_entrepreneur() {
        var partner = factory.manufacturePojo(PartnerCreateFullModel.class)
            .legalForm(LegalForm.ENTREPRENEUR)
            .accounts(
                Set.of(
                    new AccountCreateFullModel()
                        .account("03202643982374972392")
                        .bank(new BankCreate()
                            .bic("044525225")
                            .bankAccount(new BankAccountCreate()
                                .bankAccount("40102000000000000000")))
                )
            );
        var validator =
            spy(new AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void accountsAttributeBalancePhysicalPerson_partnerCreateFullModelDto_legalEntity() {
        var partner = factory.manufacturePojo(PartnerCreateFullModel.class)
            .legalForm(LegalForm.LEGAL_ENTITY)
            .accounts(
                Set.of(
                    new AccountCreateFullModel()
                        .account("03202643982374972392")
                        .bank(new BankCreate()
                            .bic("044525225")
                            .bankAccount(new BankAccountCreate()
                                .bankAccount("40102000000000000000")))
                )
            );
        var validator =
            spy(new AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator(partnerService));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertTrue(validator.isValid(partner, context));
    }
}
