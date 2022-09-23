package ru.sberbank.pprb.sbbol.partners.validation.account;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import java.util.regex.Pattern;

public class BaseTreasuryAccountValidator extends BaseValidator {
    private static final Pattern BUDGET_CORR_ACCOUNT_PATTERN = Pattern.compile("^40102\\d{15}$");
    private static final Pattern BUDGET_ACCOUNT_PATTERN_FOR_BAL1 = Pattern.compile("^0\\d{19}$");
    private static final Pattern BUDGET_ACCOUNT_PATTERN_FOR_CODE_CURRENCY = Pattern.compile("^\\d{5}643\\d{12}$");

    private boolean isBudgetCorrAccount(String account, String corrAccount) {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(corrAccount)) {
            return false;
        }
        return BUDGET_CORR_ACCOUNT_PATTERN.matcher(corrAccount).matches();
    }

    protected boolean validateBalance(String account, String corrAccount) {
        if (!isBudgetCorrAccount(account, corrAccount)) {
            return true;
        }
        return BUDGET_ACCOUNT_PATTERN_FOR_BAL1.matcher(account).matches();
    }

    protected boolean validateCodeCurrency(String account, String corrAccount) {
        if (!isBudgetCorrAccount(account, corrAccount)) {
            return true;
        }
        return BUDGET_ACCOUNT_PATTERN_FOR_CODE_CURRENCY.matcher(account).matches();
    }
}
