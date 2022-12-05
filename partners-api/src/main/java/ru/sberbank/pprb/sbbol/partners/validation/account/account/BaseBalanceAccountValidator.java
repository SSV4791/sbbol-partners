package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

@SuppressWarnings("java:S1166")
public class BaseBalanceAccountValidator extends BaseValidator {

    private static final String MESSAGE_ENTITY_NOT_FOUND = "{error.message.entity.not_found}";
    private static final String MESSAGE_INVALID_ACCOUNT_PHYSICAL_PERSON = "{validation.account.physical_person.account.invalid_account}";
    private static final String MESSAGE_INVALID_ACCOUNT_ENTREPRENEUR = "{validation.account.entrepreneur.account.invalid_account}";
    private final PartnerService partnerService;

    public BaseBalanceAccountValidator(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    public boolean isValid(ConstraintValidatorContext context, String account, String digitalId, String partnerId) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        Partner partner;
        try {
            partner = partnerService.getPartner(digitalId, partnerId);
        } catch (EntryNotFoundException e) {
            buildMessage(context, "account", MESSAGE_ENTITY_NOT_FOUND);
            return false;
        }
        return isValid(context, "account", account, partner.getLegalForm());
    }

    public boolean isValid(ConstraintValidatorContext context, String field, String account, LegalForm legalForm) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        if (!"408".equals(account.substring(0, 3))) {
            if (legalForm == LegalForm.PHYSICAL_PERSON) {
                buildMessage(context, field, MESSAGE_INVALID_ACCOUNT_PHYSICAL_PERSON);
                return false;
            }
            if (legalForm == LegalForm.ENTREPRENEUR) {
                buildMessage(context, field, MESSAGE_INVALID_ACCOUNT_ENTREPRENEUR);
                return false;
            }
        }
        return true;
    }
}
