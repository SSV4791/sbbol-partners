package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BudgetMaskMapper;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Loggable
public class BudgetMaskServiceImpl implements BudgetMaskService {

    private final BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;
    private final BudgetMaskMapper budgetMaskMapper;

    public BudgetMaskServiceImpl(
        BudgetMaskDictionaryRepository budgetMaskDictionaryRepository,
        BudgetMaskMapper budgetMaskMapper
    ) {
        this.budgetMaskDictionaryRepository = budgetMaskDictionaryRepository;
        this.budgetMaskMapper = budgetMaskMapper;
    }

    @Override
    @Transactional
    public BudgetMask saveBudgetMask(BudgetMask budgetMask) {
        var budgetMaskEntity = budgetMaskMapper.toBudgetMask(budgetMask);
        var saved = budgetMaskDictionaryRepository.save(budgetMaskEntity);
        return budgetMaskMapper.toBudgetMask(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetMasksResponse getBudgetMasks(BudgetMaskFilter budgetMaskFilter) {
        var response = budgetMaskDictionaryRepository.findByFilter(budgetMaskFilter);
        BudgetMasksResponse budgetMasksResponse = new BudgetMasksResponse();
        for (var budgetMaskEntity : response) {
            budgetMasksResponse.addMasksItem(budgetMaskMapper.toBudgetMask(budgetMaskEntity));
        }
        return budgetMasksResponse;
    }

    @Override
    @Transactional
    public void deleteBudgetMasks(List<String> ids) {
        var uuids = ids.stream()
            .map(UUID::fromString)
            .collect(Collectors.toList());
        for (UUID uuid : uuids) {
            var foundMask = budgetMaskDictionaryRepository.getByUuid(uuid);
            if (foundMask.isEmpty()) {
                throw new EntryNotFoundException("budget_mask", uuid);
            }
            budgetMaskDictionaryRepository.delete(foundMask.get());
        }
    }

    @Override
    public boolean isBudget(String account, String bic, String bankAccount) {
        boolean isGisGmpReceiver = isBicGisGmp(bic) && isAccountGisGmp(account);
        boolean isTaxAccountReceiver = isTaxAccountReceiver(account);
        boolean isOfkReceiver = isOfkReceiver(account, bankAccount);
        return !isTaxAccountReceiver && (isGisGmpReceiver || isOfkReceiver);
    }

    @Override
    public boolean isBicGisGmp(String bic) {
        if (!StringUtils.hasText(bic)) {
            return false;
        }
        var masks = budgetMaskDictionaryRepository.findAllByType(BudgetMaskType.BIC);
        return StringUtils.hasText(bic) && checkMask(bic, masks);
    }

    @Override
    public boolean isAccountGisGmp(String account) {
        if (!StringUtils.hasText(account)) {
            return false;
        }
        var masks = budgetMaskDictionaryRepository.findAllByType(BudgetMaskType.GIS_GMP_ACCOUNT);
        return checkMask(account, masks);
    }

    @Override
    public boolean isOfkReceiver(String accountNumber, String bankAccount) {
        if (StringUtils.hasText(accountNumber) && StringUtils.hasText(bankAccount)) {
            return false;
        }
        var accountMasks = budgetMaskDictionaryRepository.findAllByType(BudgetMaskType.BUDGET_ACCOUNT);
        var corrAccountMasks = budgetMaskDictionaryRepository.findAllByType(BudgetMaskType.BUDGET_CORR_ACCOUNT);
        return checkMask(accountNumber, accountMasks) && checkMask(bankAccount, corrAccountMasks);
    }

    @Override
    public boolean isTaxAccountReceiver(String bankAccount) {
        if (!StringUtils.hasText(bankAccount)) {
            return false;
        }
        var masks = budgetMaskDictionaryRepository.findAllByType(BudgetMaskType.TAX_ACCOUNT_RECEIVER);
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
        if (!StringUtils.hasText(param) || !CollectionUtils.isEmpty(masks)) {
            return false;
        }
        MaskFormatter maskFormatter = new MaskFormatter();
        for (var mask : masks) {
            try {
                maskFormatter.setMask(mask.getMask());
                maskFormatter.valueToString(param);
                return true;
            } catch (ParseException e) {
                throw new BadRequestException(e.getLocalizedMessage());
            }
        }
        return false;
    }
}
