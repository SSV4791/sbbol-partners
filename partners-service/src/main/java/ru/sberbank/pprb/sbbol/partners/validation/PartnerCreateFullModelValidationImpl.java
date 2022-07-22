package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hibernate.internal.util.StringHelper.isNotEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl.DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER;
import static ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl.DEFAULT_MESSAGE_ACCOUNT_IS_NULL;
import static ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl.DEFAULT_MESSAGE_ACCOUNT_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl.DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER;
import static ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl.DEFAULT_MESSAGE_BIC_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.DocumentCreateValidationImpl.DEFAULT_MESSAGE_ERROR_DOCUMENT_TYPE;
import static ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl.DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER;
import static ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl.DEFAULT_MESSAGE_INN_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl.DEFAULT_MESSAGE_KPP_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl.DEFAULT_MESSAGE_OGRN_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl.DEFAULT_MESSAGE_OKPO_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.ACCOUNT_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.BIC_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.RKC_BIC;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateBankAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateUserAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkInn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkOgrn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PartnerCreateFullModelValidationImpl extends AbstractValidatorImpl<PartnerCreateFullModel> {

    private final DocumentDictionaryRepository documentDictionaryRepository;

    public PartnerCreateFullModelValidationImpl(DocumentDictionaryRepository documentDictionaryRepository) {
        this.documentDictionaryRepository = documentDictionaryRepository;
    }

    @Override
    public void validator(Map<String, List<String>> errors, PartnerCreateFullModel entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getFirstName()) && entity.getFirstName().length() > FIRST_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_firstName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getMiddleName()) && entity.getOrgName().length() > MIDDLE_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_middleName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getSecondName()) && entity.getSecondName().length() > SECOND_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_secondName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_PARTNER_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_comment", MessagesTranslator.toLocale(DEFAULT_LENGTH, "255"));
        }
        if (StringUtils.isNotEmpty(entity.getOkpo())) {
            if (entity.getOkpo().length() > OKPO_PARTNER_MAX_LENGTH_VALIDATION && entity.getOkpo().length() < OKPO_PARTNER_MIN_LENGTH_VALIDATION) {
                setError(errors, "okpo", MessagesTranslator.toLocale(DEFAULT_MESSAGE_OKPO_LENGTH));
            }
        }
        if (StringUtils.isNotEmpty(entity.getOgrn())) {
            if (entity.getOgrn().length() != OGRN_PARTNER_MAX_LENGTH_VALIDATION && entity.getOgrn().length() != OGRN_PARTNER_MIN_LENGTH_VALIDATION) {
                setError(errors, "ogrn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_OGRN_LENGTH));
            }
        }
        if (StringUtils.isNotEmpty(entity.getKpp()) && entity.getKpp().length() != BasePartnerValidation.KPP_VALID_LENGTH) {
            setError(errors, "kpp", MessagesTranslator.toLocale(DEFAULT_MESSAGE_KPP_LENGTH));
        }
        if (entity.getLegalForm() == null) {
            setError(errors, "partner_legalForm", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "правовую форму партнёра"));
        } else {
            checkLegalFormProperty(entity, errors);
        }
        if (entity.getEmails() != null) {
            for (var email : entity.getEmails()) {
                commonValidationChildEmail(errors, email);
            }
        }
        if (entity.getPhones() != null) {
            for (var phone : entity.getPhones()) {
                commonValidationChildPhone(errors, phone);
            }
        }
        if (!CollectionUtils.isEmpty(entity.getAccounts())) {
            for (AccountCreateFullModel account : entity.getAccounts()) {
                validatorAccount(errors, account);
            }
        }
        if (!CollectionUtils.isEmpty(entity.getAddress())) {
            for (AddressCreateFullModel address : entity.getAddress()) {
                validatorAddress(errors, address);
            }
        }
        if (!CollectionUtils.isEmpty(entity.getDocuments())) {
            for (DocumentCreateFullModel document : entity.getDocuments()) {
                validatorDocument(errors, document);
            }
        }
        if (!CollectionUtils.isEmpty(entity.getContacts())) {
            for (ContactCreateFullModel contact : entity.getContacts()) {
                validatorContact(errors, contact);
            }
        }
    }

    private void checkLegalFormProperty(PartnerCreateFullModel entity, Map<String, List<String>> errors) {
        if (entity.getLegalForm() == LegalForm.LEGAL_ENTITY) {
            if (StringUtils.isEmpty(entity.getOrgName())) {
                setError(errors, "partner_orgName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "название организации"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 10 && entity.getInn().length() != 5) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "10"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
            }
            if (StringUtils.isNotEmpty(entity.getOgrn()) && !checkOgrn(entity.getOgrn())) {
                setError(errors, "ogrn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ОГРН"));
            }
        } else if (entity.getLegalForm() == LegalForm.ENTREPRENEUR) {
            if (StringUtils.isEmpty(entity.getOrgName())) {
                setError(errors, "partner_orgName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "название организации"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12 && entity.getInn().length() != 5) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "12"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
            }
            if (StringUtils.isNotEmpty(entity.getOgrn()) && !checkOgrn(entity.getOgrn())) {
                setError(errors, "orgn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ОГРН"));
            }
        } else if (entity.getLegalForm() == LegalForm.PHYSICAL_PERSON) {
            if (StringUtils.isEmpty(entity.getFirstName())) {
                setError(errors, "partner_firstName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "имя партнёра"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12 && entity.getInn().length() != 5) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "12"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
            }
        }
    }

    private void validatorAccount(Map<String, List<String>> errors, AccountCreateFullModel entity) {
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_MAX_LENGTH_VALIDATION) {
            setError(errors, "account_comment", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getAccount()) && entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_LENGTH));
        }
        if (entity.getBank() == null) {
            setError(errors, "bank", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "реквизиты банка"));
        } else {
            checkBank(entity, errors);
        }
    }

    private void checkBank(AccountCreateFullModel entity, Map<String, List<String>> errors) {
        var bank = entity.getBank();
        if (bank.getBic() == null) {
            setError(errors, "bic", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "реквизиты банка"));
        }
        if (bank.getBic().length() != BIC_VALID_LENGTH) {
            setError(errors, "bic", DEFAULT_MESSAGE_BIC_LENGTH);
        }
        if (StringUtils.isNotEmpty(bank.getName()) && bank.getName().length() > BANK_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "bankName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "160"));
        }
        if (bank.getName() == null) {
            setError(errors, "bankName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "название банка"));
        }
        var matchBic = RKC_BIC.matcher(bank.getBic());
        if (matchBic.matches()) {
            if (StringUtils.isNotEmpty(entity.getAccount()) && !validateBankAccount(entity.getAccount(), bank.getBic())) {
                setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER));
            }
        } else if (StringUtils.isNotEmpty(entity.getAccount()) && !validateUserAccount(entity.getAccount(), bank.getBic())) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER));
        }
        if (bank.getBankAccount() != null) {
            checkBankAccount(bank, errors);
        }
    }

    private void checkBankAccount(BankCreate bank, Map<String, List<String>> errors) {
        var bankAccount = bank.getBankAccount();
        if (StringUtils.isEmpty(bankAccount.getBankAccount())) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "корреспондентский счёт"));
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && bankAccount.getBankAccount().length() != ACCOUNT_VALID_LENGTH) {
            setError(errors, "bankAccount", DEFAULT_MESSAGE_ACCOUNT_LENGTH);
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && !validateBankAccount(bankAccount.getBankAccount(), bank.getBic())) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER));
        }
    }

    private void validatorAddress(Map<String, List<String>> errors, AddressCreateFullModel entity) {
        if (StringUtils.isNotEmpty(entity.getZipCode()) && entity.getZipCode().length() > ZIP_CODE_MAX_LENGTH_VALIDATION) {
            setError(errors, "zipCode", MessagesTranslator.toLocale(DEFAULT_LENGTH, "6"));
        }
        if (StringUtils.isNotEmpty(entity.getRegion()) && entity.getRegion().length() > REGION_MAX_LENGTH_VALIDATION) {
            setError(errors, "region", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getRegionCode()) && entity.getRegionCode().length() > REGION_CODE_MAX_LENGTH_VALIDATION) {
            setError(errors, "regionCode", MessagesTranslator.toLocale(DEFAULT_LENGTH, "10"));
        }
        if (StringUtils.isNotEmpty(entity.getCity()) && entity.getCity().length() > CITY_MAX_LENGTH_VALIDATION) {
            setError(errors, "city", MessagesTranslator.toLocale(DEFAULT_LENGTH, "300"));
        }
        if (StringUtils.isNotEmpty(entity.getLocation()) && entity.getLocation().length() > LOCATION_MAX_LENGTH_VALIDATION) {
            setError(errors, "location", MessagesTranslator.toLocale(DEFAULT_LENGTH, "300"));
        }
        if (StringUtils.isNotEmpty(entity.getStreet()) && entity.getStreet().length() > STREET_MAX_LENGTH_VALIDATION) {
            setError(errors, "street", MessagesTranslator.toLocale(DEFAULT_LENGTH, "300"));
        }
        if (StringUtils.isNotEmpty(entity.getBuilding()) && entity.getBuilding().length() > BUILDING_MAX_LENGTH_VALIDATION) {
            setError(errors, "building", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
        if (StringUtils.isNotEmpty(entity.getBuildingBlock()) && entity.getBuildingBlock().length() > BUILDING_BLOCK_MAX_LENGTH_VALIDATION) {
            setError(errors, "buildingBlock", MessagesTranslator.toLocale(DEFAULT_LENGTH, "20"));
        }
        if (StringUtils.isNotEmpty(entity.getFlat()) && entity.getFlat().length() > FLAT_MAX_LENGTH_VALIDATION) {
            setError(errors, "flat", MessagesTranslator.toLocale(DEFAULT_LENGTH, "20"));
        }
        if (entity.getType() == null) {
            setError(errors, "type", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "Тип адреса"));
        }
    }
    private void validatorDocument(Map<String, List<String>> errors, DocumentCreateFullModel entity) {
        if (isNotEmpty(entity.getDocumentTypeId())) {
            var foundDocumentDictionary = documentDictionaryRepository.getByUuid(UUID.fromString(entity.getDocumentTypeId()));
            if (foundDocumentDictionary.isEmpty()) {
                setError(errors, "documentType", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ERROR_DOCUMENT_TYPE));
            }
        } else {
            setError(errors, "documentType", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "тип документа"));
        }
        if (StringUtils.isNotEmpty(entity.getSeries()) && entity.getSeries().length() > SERIES_MAX_LENGTH_VALIDATION) {
            setError(errors, "series", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getNumber()) && entity.getNumber().length() > NUMBER_MAX_LENGTH_VALIDATION) {
            setError(errors, "number", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getDivisionIssue()) && entity.getDivisionIssue().length() > DIVISION_ISSUE_MAX_LENGTH_VALIDATION) {
            setError(errors, "divisionIssue", MessagesTranslator.toLocale(DEFAULT_LENGTH, "250"));
        }
        if (StringUtils.isNotEmpty(entity.getDivisionCode()) && entity.getDivisionCode().length() > DIVISION_CODE_MAX_LENGTH_VALIDATION) {
            setError(errors, "divisionCode", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getCertifierName()) && entity.getCertifierName().length() > CERTIFIER_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "certifierName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
        if (StringUtils.isNotEmpty(entity.getPositionCertifier()) && entity.getPositionCertifier().length() > POSITION_CERTIFIER_MAX_LENGTH_VALIDATION) {
            setError(errors, "positionCertifier", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
    }

    private void validatorContact(Map<String, List<String>> errors, ContactCreateFullModel entity) {
        if (StringUtils.isNotEmpty(entity.getFirstName()) && entity.getFirstName().length() > FIRST_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_firstName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getOrgName()) && entity.getOrgName().length() > ORG_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_orgName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "350"));
        }
        if (StringUtils.isNotEmpty(entity.getSecondName()) && entity.getSecondName().length() > SECOND_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_secondName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getMiddleName()) && entity.getMiddleName().length() > MIDDLE_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_middleName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getPosition()) && entity.getPosition().length() > POSITION_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_position", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
        if (entity.getLegalForm() == null) {
            setError(errors, "contact_legalForm", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "правовую форму контакта"));
        }
        if (entity.getEmails() != null) {
            for (var email : entity.getEmails()) {
                commonValidationChildEmail(errors, email);
            }
        }
        if (entity.getPhones() != null) {
            for (var phone : entity.getPhones()) {
                commonValidationChildPhone(errors, phone);
            }
        }
    }
}
