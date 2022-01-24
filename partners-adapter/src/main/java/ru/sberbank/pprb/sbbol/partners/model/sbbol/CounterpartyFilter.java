package ru.sberbank.pprb.sbbol.partners.model.sbbol;

/**
 * Фильтр получения пагинированного списка контрагентов
 */
public class CounterpartyFilter extends AbstractFilter {

    /**
     * Признак выборки подписанных контрагентов
     */
    private Boolean signed;

    /**
     * Cтрока поиска для отбора элементов справочника контрагентов. Значением строки может быть наименование, ИНН и т.д.
     */
    private String searchPattern;

    /**
     * Признак выборки контрагентов для платежей ЖКХ
     */
    private Boolean isHousingPaymentReceiever;

    public Boolean getSigned() {
        return signed;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public Boolean getHousingPaymentReceiever() {
        return isHousingPaymentReceiever;
    }

    public void setHousingPaymentReceiever(Boolean housingPaymentReceiever) {
        isHousingPaymentReceiever = housingPaymentReceiever;
    }
}
