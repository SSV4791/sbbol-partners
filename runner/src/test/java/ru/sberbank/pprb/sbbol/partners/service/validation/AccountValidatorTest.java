package ru.sberbank.pprb.sbbol.partners.service.validation;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidator;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTestLayer
class AccountValidatorTest {

    @Test
    void testUserAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(AccountValidator.class, "validateUserAccount", "40802810500490014206", "044525411"));
    }

    @Test
    void testUserAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(AccountValidator.class, "validateUserAccount", "30101810145250000411", "044525411"));
    }

    @Test
    void testBankAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(AccountValidator.class, "validateBankAccount", "30101810145250000411", "044525411"));
    }

    @Test
    void testBankAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(AccountValidator.class, "validateBankAccount", "40802810500490014206", "044525411"));
    }
}
