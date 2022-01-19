package ru.sberbank.pprb.sbbol.partners.validation;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class PartnerAccountValidatorTest {

    @Test
    void testUserAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(PartnerAccountValidator.class, "userAccountValid", "40802810500490014206", "044525411"));
    }

    @Test
    void testUserAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(PartnerAccountValidator.class, "userAccountValid", "30101810145250000411", "044525411"));
    }

    @Test
    void testBankAccountValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(PartnerAccountValidator.class, "bankAccountValid", "30101810145250000411", "044525411"));
    }

    @Test
    void testBankAccountNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(PartnerAccountValidator.class, "bankAccountValid", "40802810500490014206", "044525411"));
    }
}
