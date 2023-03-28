package ru.sberbank.pprb.sbbol.partners.validation.account;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import java.util.regex.Pattern;

public class BaseAccountRubCodeCurrencyValidator extends BaseValidator {
    private static final Pattern RUR_ACCOUNT_PATTERN = Pattern.compile("^.{5}810.*$");
    private static final Pattern BUDGET_CORR_ACCOUNT_PATTERN = Pattern.compile("^40102\\d{15}$");

    protected boolean validate(String account, String corrAccount) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        if (RUR_ACCOUNT_PATTERN.matcher(account).matches()) {
            return true;
        }
        return StringUtils.isNotEmpty(corrAccount) && BUDGET_CORR_ACCOUNT_PATTERN.matcher(corrAccount).matches();
    }
}
