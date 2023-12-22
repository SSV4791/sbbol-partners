package ru.sberbank.pprb.sbbol.partners.validator;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.config.ServiceConfiguration;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.name.NameAttributePartnerCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.name.NameAttributePartnerDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.partner.name.NameAttributePartnerCreateFullModelDtoValidator;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@ContextConfiguration(
    classes = ServiceConfiguration.class
)
class PartnerNameValidationTest extends BaseUnitConfiguration {

    @Autowired
    private LegalFormInspector legalFormInspector;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void nameAttribute_partnerDto_physicalPerson_secondNameIsNull() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .secondName(null)
            .legalForm(LegalForm.PHYSICAL_PERSON);

        var validator =
            spy(new NameAttributePartnerDtoValidator());
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerDto_physicalPerson_firstNameIsNull() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .firstName(null)
            .legalForm(LegalForm.PHYSICAL_PERSON);

        var validator =
            spy(new NameAttributePartnerDtoValidator());
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerDto_physicalPerson_secondAndFirstNameIsNull() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .firstName(null)
            .secondName(null)
            .legalForm(LegalForm.PHYSICAL_PERSON);

        var validator =
            spy(new NameAttributePartnerDtoValidator());
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerDto_legalEntity_orgNameIsNull() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .orgName(null)
            .legalForm(LegalForm.LEGAL_ENTITY);

        var validator =
            spy(new NameAttributePartnerDtoValidator());
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerDto_entrepreneur_orgNameIsNull() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .orgName(null)
            .legalForm(LegalForm.ENTREPRENEUR);

        var validator =
            spy(new NameAttributePartnerDtoValidator());
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerCreateDto_physicalPerson_firstNameIsNull() {
        PartnerCreate partner = factory.manufacturePojo(PartnerCreate.class)
            .firstName(null)
            .legalForm(LegalForm.PHYSICAL_PERSON);

        var validator =
            spy(new NameAttributePartnerCreateDtoValidator(legalFormInspector));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerCreateDto_legalEntity_orgNameIsNull() {
        PartnerCreate partner = factory.manufacturePojo(PartnerCreate.class)
            .orgName(null)
            .legalForm(LegalForm.LEGAL_ENTITY);

        var validator =
            spy(new NameAttributePartnerCreateDtoValidator(legalFormInspector));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerCreateDto_entrepreneur_orgNameIsNull() {
        PartnerCreate partner = factory.manufacturePojo(PartnerCreate.class)
            .orgName(null)
            .legalForm(LegalForm.ENTREPRENEUR);

        var validator =
            spy(new NameAttributePartnerCreateDtoValidator(legalFormInspector));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerFullModelDto_physicalPerson_firstNameIsNull() {
        PartnerCreateFullModel partner = factory.manufacturePojo(PartnerCreateFullModel.class)
            .firstName(null)
            .legalForm(LegalForm.PHYSICAL_PERSON);

        var validator =
            spy(new NameAttributePartnerCreateFullModelDtoValidator(legalFormInspector));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerFullModelDto_legalEntity_orgNameIsNull() {
        PartnerCreateFullModel partner = factory.manufacturePojo(PartnerCreateFullModel.class)
            .orgName(null)
            .legalForm(LegalForm.LEGAL_ENTITY);

        var validator =
            spy(new NameAttributePartnerCreateFullModelDtoValidator(legalFormInspector));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }

    @Test
    void nameAttribute_partnerFullModelDto_entrepreneur_orgNameIsNull() {
        PartnerCreateFullModel partner = factory.manufacturePojo(PartnerCreateFullModel.class)
            .orgName(null)
            .legalForm(LegalForm.ENTREPRENEUR);

        var validator =
            spy(new NameAttributePartnerCreateFullModelDtoValidator(legalFormInspector));
        doNothing()
            .when((BaseValidator) validator).buildMessage(any(), any(), any());
        assertFalse(validator.isValid(partner, context));
    }
}
