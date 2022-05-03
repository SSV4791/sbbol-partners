package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMask;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskForm;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMasksResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetMaskDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/budget-mask";

    @Autowired
    private PodamFactory podamFactory;
    @Autowired
    private BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;

    private BudgetMask budgetMask;

    @AfterAll
    public void dropTestData() {
        if (budgetMask != null) {
            budgetMaskDictionaryRepository.deleteById(UUID.fromString(budgetMask.getId()));
        }
    }

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
        var mask = getBudgetMask(BudgetMaskForm.BIC);
        budgetMask = post(
            baseRoutePath,
            HttpStatus.CREATED,
            mask,
            BudgetMask.class
        );
        assertThat(budgetMask)
            .isNotNull();
        assertThat(budgetMask.getMask())
            .isEqualTo(mask.getMask());
        var filter1 = new BudgetMaskFilter()
            .maskType(BudgetMaskForm.BIC);
        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, filter1, BudgetMasksResponse.class);
        assertThat(searchDocument.getMasks())
            .contains(budgetMask);
    }

    @Test
    @AllureId("34193")
    void testDeleteBudgetMasks() {
        var budgetMask = getBudgetMask(BudgetMaskForm.BUDGET_CORR_ACCOUNT);
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

    private BudgetMask getBudgetMask(BudgetMaskForm maskForm) {
        var mask = podamFactory.manufacturePojo(BudgetMask.class);
        if (maskForm != null) {
            mask.setMaskType(maskForm);
        }
        return mask;
    }
}
