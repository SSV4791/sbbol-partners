package ru.sberbank.pprb.sbbol.partners.service.validation;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTestLayer
class AccountUpdateValidatorImplTest {

    @Test
    @AllureId("34104")
    void testUserAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerAccountValidation.class, "validateUserAccount", "40802810500490014206", "044525411"));
    }

    @Test
    @AllureId("34063")
    void testUserAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerAccountValidation.class, "validateUserAccount", "30101810145250000411", "044525411"));
    }

    @Test
    @AllureId("34044")
    void testBankAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerAccountValidation.class, "validateBankAccount", "30101810145250000411", "044525411"));
    }

    @Test
    @AllureId("34053")
    void testBankAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerAccountValidation.class, "validateBankAccount", "40802810500490014206", "044525411"));
    }
}
