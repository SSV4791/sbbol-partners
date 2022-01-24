package ru.sberbank.pprb.sbbol.partners.entity.partner.enums;

/**
 * Типы бюджетных масок
 */
public enum BudgetMaskType {

    GIS_GMP_ACCOUNT("Маска ГИС ГМП"),
    BIC("Маска БИК"),
    BUDGET_CORR_ACCOUNT("Маска кор. счёта"),
    TAX_ACCOUNT_RECEIVER("Маска ИНН"),
    BUDGET_ACCOUNT("Маска счёт"),
    ;

    private final String desc;

    BudgetMaskType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
