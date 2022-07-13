package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.BudgetMaskDictionaryApi;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.List;

@RestController
public class BudgetMaskDictionaryController implements BudgetMaskDictionaryApi {

    private final BudgetMaskService budgetMaskService;

    public BudgetMaskDictionaryController(BudgetMaskService budgetMaskService) {
        this.budgetMaskService = budgetMaskService;
    }

    @Override
    public ResponseEntity<BudgetMask> create(BudgetMask budgetMask) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetMaskService.saveBudgetMask(budgetMask));
    }

    @Override
    public ResponseEntity<Void> delete(List<String> ids) {
        budgetMaskService.deleteBudgetMasks(ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BudgetMasksResponse> list(BudgetMaskFilter budgetMaskFilter) {
        return ResponseEntity.ok(budgetMaskService.getBudgetMasks(budgetMaskFilter));
    }
}
