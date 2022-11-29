package ru.sberbank.pprb.sbbol.partners.validation.account;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import java.util.regex.Pattern;

public class BaseAccountRubCodeCurrencyValidator extends BaseValidator {
    private static final Pattern BUDGET_ACCOUNT_PATTERN = Pattern.compile("^0\\d{4}643\\d{12}$");
    private static final Pattern RUR_ACCOUNT_PATTERN = Pattern.compile("^.{5}810.*$");

    protected boolean validate(String account) {
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        if (BUDGET_ACCOUNT_PATTERN.matcher(account).matches()) {
            return true;
        }
        return RUR_ACCOUNT_PATTERN.matcher(account).matches();
    }
}
