package ru.sberbank.pprb.sbbol.partners.service.validation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountKeyValidator;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTestLayer
class BaseAccountKeyValidatorTest {

    BaseAccountKeyValidator baseAccountKeyValidator = new BaseAccountKeyValidator();

    @Test
    void testUserAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(baseAccountKeyValidator, "validateUserAccount",  "40802810500490014206", "044525411"));
    }

    @Test
    void testUserAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(baseAccountKeyValidator, "validateUserAccount", "30101810145250000411", "044525411"));
    }

    @Test
    void testBankAccountValid() {
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(baseAccountKeyValidator, "validateBankAccount", "30101810145250000411", "044525411"));
    }

    @Test
    void testBankAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(baseAccountKeyValidator, "validateBankAccount", "40802810500490014206", "044525411"));
    }
}
