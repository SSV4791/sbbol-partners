package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskForm;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetMaskDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/budget-mask";

    @Test
    void testViewBudgetMasks() {
        var filter1 = new BudgetMaskFilter()
            .maskType(BudgetMaskForm.BUDGET_ACCOUNT)
            .pagination(new Pagination()
                .count(4)
                .offset(0)
            );
        var response = post(
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter1,
            BudgetMasksResponse.class
        );
        assertThat(response)
            .isNotNull();
    }
}
