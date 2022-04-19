package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskForm;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetMaskDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/budget-mask";

    @Test
    @AllureId("34157")
    void testViewBudgetMasks() {
        var filter1 = new BudgetMaskFilter()
            .maskType(BudgetMaskForm.BUDGET_ACCOUNT);
        var response = post(
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter1,
            BudgetMasksResponse.class
        );
        assertThat(response)
            .isNotNull();
    }

    @Test
    @AllureId("34205")
    void testCreateBudgetMasks() {
        var mask = "Новая маска";
        var budgetMask = new BudgetMask()
            .id(UUID.randomUUID().toString())
            .mask(mask)
            .condition(mask)
            .maskType(BudgetMaskForm.BIC);
        var saveDocument = post(
            baseRoutePath,
            HttpStatus.CREATED,
            budgetMask,
            BudgetMask.class
        );
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getMask())
            .isEqualTo(mask);
        var filter1 = new BudgetMaskFilter()
            .maskType(BudgetMaskForm.BIC);
        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, filter1, BudgetMasksResponse.class);
        assertThat(searchDocument.getMasks())
            .contains(saveDocument);
    }

    @Test
    @AllureId("34193")
    void testDeleteBudgetMasks() {
        var budgetMask = new BudgetMask()
            .id(UUID.randomUUID().toString())
            .mask("Рик и Морти")
            .condition("%Рик и Морти%")
            .maskType(BudgetMaskForm.BUDGET_CORR_ACCOUNT);
        var saveDocument = post(baseRoutePath, HttpStatus.CREATED, budgetMask, BudgetMask.class);
        assertThat(saveDocument)
            .isNotNull();

        var filter1 = new BudgetMaskFilter()
            .maskType(BudgetMaskForm.BUDGET_CORR_ACCOUNT);
        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, filter1, BudgetMasksResponse.class);
        assertThat(searchDocument.getMasks())
            .contains(saveDocument);

        var deleteBudgetMask =
            delete(
                baseRoutePath + "/{id}",
                HttpStatus.NO_CONTENT,
                saveDocument.getId()
            ).getBody();
        assertThat(deleteBudgetMask)
            .isNotNull();

        var newSearchDocument = post(baseRoutePath + "/view", HttpStatus.OK, filter1, BudgetMasksResponse.class);
        assertThat(newSearchDocument.getMasks())
            .doesNotContain(saveDocument);
    }
}
