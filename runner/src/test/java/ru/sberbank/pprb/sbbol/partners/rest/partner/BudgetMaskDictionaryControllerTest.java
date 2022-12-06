package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("POST /dictionary/budget-mask/view Фильтрация по бюджеткой маске")
    void testViewBudgetMasks() {
        var filter = Allure.step("Подготовка фильтра с маской бюджетного счёта", () -> {
            return new BudgetMaskFilter()
                .maskType(BudgetMaskForm.BUDGET_ACCOUNT)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)
                );
        });
        var response = Allure.step("Выполнение post-запроса /dictionary/budget-mask/view, код ответа 200", () -> post(
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter,
            BudgetMasksResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
        });
    }
}
