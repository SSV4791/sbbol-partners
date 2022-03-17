package ru.sberbank.pprb.sbbol.partners.validation;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import static org.assertj.core.api.Assertions.assertThat;

class PartnerValidatorTest {

    @Test
    void testCheckInn10Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "4139314257"));
    }

    @Test
    void testCheckInn10SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "4139314258"));
    }

    @Test
    void testCheckInn12Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "590850073854"));
    }

    @Test
    void testCheckInn12SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "59085007385"));
    }

    @Test
    void testCheckOgrn13Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "1035006110083"));
    }

    @Test
    void testCheckOgrn13SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "1035006110084"));
    }

    @Test
    void testCheckOgrn15Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "304463210700212"));
    }

    @Test
    void testCheckOgrn15SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "304463210700214"));
    }
}
