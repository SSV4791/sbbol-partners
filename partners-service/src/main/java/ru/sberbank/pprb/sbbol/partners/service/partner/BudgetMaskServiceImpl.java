package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BudgetMaskMapper;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.storage.BudgetMaskCacheableStorage;

import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.StringUtils.hasText;

@Loggable
public class BudgetMaskServiceImpl implements BudgetMaskService {

    private final BudgetMaskCacheableStorage budgetMaskCacheableStorage;

    private final BudgetMaskMapper budgetMaskMapper;

    public BudgetMaskServiceImpl(
        BudgetMaskCacheableStorage budgetMaskCacheableStorage,
        BudgetMaskMapper budgetMaskMapper
    ) {
        this.budgetMaskCacheableStorage = budgetMaskCacheableStorage;
        this.budgetMaskMapper = budgetMaskMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetMasksResponse getBudgetMasks(BudgetMaskFilter filter) {
        var response = budgetMaskCacheableStorage.findByFilter(filter);
        BudgetMasksResponse budgetMasksResponse = new BudgetMasksResponse();
        for (var budgetMaskEntity : response) {
            budgetMasksResponse.addMasksItem(budgetMaskMapper.toBudgetMask(budgetMaskEntity));
        }
        var pagination = filter.getPagination();
        budgetMasksResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            budgetMasksResponse.getPagination().hasNextPage(Boolean.TRUE);
            budgetMasksResponse.getMasks().remove(size - 1);
        }
        return budgetMasksResponse;
    }

    @Override
    public boolean isBudget(String account, String bic, String bankAccount) {
        boolean isGisGmpReceiver = isBicGisGmp(bic) && isAccountGisGmp(account);
        boolean isTaxAccountReceiver = isTaxAccountReceiver(account);
        boolean isOfkReceiver = isOfkReceiver(account, bankAccount);
        return !isTaxAccountReceiver && (isGisGmpReceiver || isOfkReceiver);
    }

    private boolean isBicGisGmp(String bic) {
        if (isBlank(bic)) {
            return false;
        }
        var masks = budgetMaskCacheableStorage.findAllByType(BudgetMaskType.BIC);
        return hasText(bic) && checkMask(bic, masks);
    }

    private boolean isAccountGisGmp(String account) {
        if (isBlank(account)) {
            return false;
        }
        var masks = budgetMaskCacheableStorage.findAllByType(BudgetMaskType.GIS_GMP_ACCOUNT);
        return checkMask(account, masks);
    }

    private boolean isOfkReceiver(String accountNumber, String bankAccount) {
        if (isBlank(accountNumber) || isBlank(bankAccount)) {
            return false;
        }
        var accountMasks = budgetMaskCacheableStorage.findAllByType(BudgetMaskType.BUDGET_ACCOUNT);
        var corrAccountMasks = budgetMaskCacheableStorage.findAllByType(BudgetMaskType.BUDGET_CORR_ACCOUNT);
        return checkMask(accountNumber, accountMasks) && checkMask(bankAccount, corrAccountMasks);
    }

    private boolean isTaxAccountReceiver(String bankAccount) {
        if (isBlank(bankAccount)) {
            return false;
        }
        var masks = budgetMaskCacheableStorage.findAllByType(BudgetMaskType.TAX_ACCOUNT_RECEIVER);
        return checkMask(bankAccount, masks);
    }

    /**
     * Метод определения подпадает ли строка под проверку по заданным маскам
     *
     * @param param номера счета или бик для проверки
     * @param masks список масок номеров счетов или бик'ов
     * @return true если счёт или бик попадает под одну из масок
     */
    private boolean checkMask(String param, List<BudgetMaskEntity> masks) {
        if (isBlank(param) || CollectionUtils.isEmpty(masks)) {
            return false;
        }
        MaskFormatter maskFormatter = new MaskFormatter();
        for (var mask : masks) {
            try {
                maskFormatter.setMask(mask.getMask());
                maskFormatter.valueToString(param);
                return true;
            } catch (ParseException ignored) {
                //Игнорируем ошибку, чтоб идти дальше по процессу.
            }
        }
        return false;
    }
}
