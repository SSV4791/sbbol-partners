package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;

/**
 * Сервис по работе с масками по определению бюджетности
 */
public interface BudgetMaskService {

    /**
     * Получение списка бюджетным масок по фильтру
     *
     * @param budgetMaskFilter фильтр для поиска бюджетных масок
     * @return список бюджетных масок
     */
    BudgetMasksResponse getBudgetMasks(BudgetMaskFilter budgetMaskFilter);

    /**
     * Проверка является ли счет бюджетным
     *
     * @param account     Счёт
     * @param bic         БИК
     * @param bankAccount кор.счёт
     * @return true - при совпадении масок, иначе - false
     */
    boolean isBudget(String account, String bic, String bankAccount);

    /**
     * Подходит ли БИК под требования ГИС ГМП
     *
     * @param bic БИК
     * @return true - при совпадении масок, иначе - false
     */
    boolean isBicGisGmp(String bic);

    /**
     * Подходит ли счёт под требования ГИС ГМП
     *
     * @param account номер счёта
     * @return true - при совпадении масок, иначе - false
     */
    boolean isAccountGisGmp(String account);

    /**
     * Проверяет является ли счёт ОФК
     *
     * @param accountNumber номер счёта
     * @param bankAccount   корр счёт банка
     * @return true - при совпадении масок, иначе - false
     */
    boolean isOfkReceiver(String accountNumber, String bankAccount);

    /**
     * Подходит ли счёт под маски счетов организации УФК
     *
     * @param bankAccount корр счёт банка
     * @return true - при совпадении масок, иначе - false
     */
    boolean isTaxAccountReceiver(String bankAccount);
}
