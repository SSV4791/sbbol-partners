package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import java.util.ArrayList;
import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkInn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkOgrn;

public class PartnerValidator implements Validator<Partner> {

    public static final String DEFAULT_MESSAGE_FIELD_IS_NULL = "default.field.is_null";
    public static final String DEFAULT_MESSAGE_FIELDS_IS_NULL = "default.fields.is_null";
    public static final String DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER = "default.field.control_number";

    public static final String LEGAL_FORM_LEGAL_ENTITY = "legalForm.LEGAL_ENTITY";
    public static final String LEGAL_FORM_ENTREPRENEUR = "legalForm.ENTREPRENEUR";
    public static final String LEGAL_FORM_PHYSICAL_PERSON = "legalForm.PHYSICAL_PERSON";


    @Override
    public List<String> validation(Partner entity) {
        var errors = new ArrayList<String>();
        if (entity.getId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "id"));
        }
        if (entity.getDigitalId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "digitalId"));
        }
        if (entity.getLegalForm() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "legalForm"));
        } else {
            checkLegalFormProperty(entity, errors);
        }
        return errors;
    }

    private void checkLegalFormProperty(Partner entity, ArrayList<String> errors) {
        if (entity.getLegalForm() == LegalForm.LEGAL_ENTITY) {
            if (entity.getOrgName() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "orgName", LEGAL_FORM_LEGAL_ENTITY));
            }
            if (entity.getInn() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "inn", LEGAL_FORM_LEGAL_ENTITY));
            } else if (!checkInn(entity.getInn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
            }
            if (entity.getKpp() != null && entity.getKpp().length() != BasePartnerValidation.KPP_VALID_LENGTH) {
                errors.add(MessagesTranslator.toLocale("partner.kpp.length"));
            }
            if (entity.getOgrn() != null && !checkOgrn(entity.getOgrn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
            }
        } else if (entity.getLegalForm() == LegalForm.ENTREPRENEUR) {
            if (entity.getOrgName() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "orgName", LEGAL_FORM_ENTREPRENEUR));
            }
            if (entity.getInn() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "inn", LEGAL_FORM_ENTREPRENEUR));
            }
            if (entity.getOgrn() != null && !checkOgrn(entity.getOgrn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
            }
        } else if (entity.getLegalForm() == LegalForm.PHYSICAL_PERSON) {
            if (entity.getFirstName() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "firstName", LEGAL_FORM_PHYSICAL_PERSON));
            }
            if (entity.getSecondName() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "secondName", LEGAL_FORM_PHYSICAL_PERSON));
            }
        }
    }
}
