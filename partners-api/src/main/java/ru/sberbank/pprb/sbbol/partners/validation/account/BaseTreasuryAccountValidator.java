package ru.sberbank.pprb.sbbol.partners.validation.account;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import java.util.UUID;
import java.util.regex.Pattern;

public class BaseTreasuryAccountValidator extends BaseValidator {
    private static final Pattern BUDGET_CORR_ACCOUNT_PATTERN = Pattern.compile("^40102\\d{15}$");
    private static final Pattern BUDGET_ACCOUNT_PATTERN_FOR_BAL1 = Pattern.compile("^0\\d{19}$");
    private static final Pattern BUDGET_ACCOUNT_PATTERN_FOR_CODE_CURRENCY = Pattern.compile("^\\d{5}643\\d{12}$");
    private static final Pattern BUDGET_ACCOUNT_PATTERN_FOR_CODE_CURRENCY_AND_BALANCE = Pattern.compile("^0\\d{4}643\\d{12}$");

    private final PartnerService partnerService;

    public BaseTreasuryAccountValidator(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    protected boolean isBudgetCorrAccount(
        String digitalId,
        UUID partnerId,
        String corrAccount
    ) {
        if (isPartnerLegalFormNotLegalEntity(digitalId, partnerId)) {
            return !isBudgetCorrAccount(corrAccount);
        }
        return true;
    }

    protected boolean isBudgetCorrAccount(String corrAccount) {
        if (StringUtils.isEmpty(corrAccount)) {
            return false;
        }
        return BUDGET_CORR_ACCOUNT_PATTERN.matcher(corrAccount).matches();
    }

    protected boolean validateBalance(
        String account,
        String corrAccount
    ) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        if (!isBudgetCorrAccount(corrAccount)) {
            return true;
        }
        return BUDGET_ACCOUNT_PATTERN_FOR_BAL1.matcher(account).matches();
    }

    protected boolean validateCodeCurrency(
        String account,
        String corrAccount
    ) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        if (!isBudgetCorrAccount(corrAccount)) {
            return true;
        }
        return BUDGET_ACCOUNT_PATTERN_FOR_CODE_CURRENCY.matcher(account).matches();
    }

    protected boolean validateCorrAccount(
        String account,
        String corrAccount
    ) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        if (!BUDGET_ACCOUNT_PATTERN_FOR_CODE_CURRENCY_AND_BALANCE.matcher(account).matches()) {
            return true;
        }
        return isBudgetCorrAccount(corrAccount);
    }

    private boolean isPartnerLegalFormNotLegalEntity(String digitalId, UUID partnerId) {
        if (ObjectUtils.isEmpty(digitalId) || ObjectUtils.isEmpty(partnerId)) {
            return true;
        }
        try {
            var partnerLegalForm = partnerService.getPartnerLegalForm(digitalId, partnerId);
            if (LegalForm.LEGAL_ENTITY != partnerLegalForm) {
                return true;
            }
        } catch (EntryNotFoundException ignore) {
            return true;
        }
        return false;
    }
}
