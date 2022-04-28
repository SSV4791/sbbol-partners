package ru.sberbank.pprb.sbbol.partners.validation;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTestLayer
class PartnerValidatorTest {

    @Test
    @AllureId("34100")
    void testCheckInn10Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "4139314257"));
    }

    @Test
    @AllureId("34100")
    void testCheckInn10SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "4139314258"));
    }

    @Test
    @AllureId("34090")
    void testCheckInn12Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "590850073854"));
    }

    @Test
    @AllureId("34072")
    void testCheckInn12SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "59085007385"));
    }

    @Test
    @AllureId("37096")
    void testCheckKio5SymbolValid() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "12345"));
    }

    @Test
    @AllureId("37097")
    void testCheckKio5SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkInn", "123456"));
    }

    @Test
    @AllureId("34108")
    void testCheckOgrn13Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "1035006110083"));
    }

    @Test
    @AllureId("34092")
    void testCheckOgrn13SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "1035006110084"));
    }

    @Test
    @AllureId("34075")
    void testCheckOgrn15Symbol() {
        assertThat(Boolean.TRUE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "304463210700212"));
    }

    @Test
    @AllureId("34101")
    void testCheckOgrn15SymbolNotValid() {
        assertThat(Boolean.FALSE)
            .isEqualTo(ReflectionTestUtils.invokeMethod(BasePartnerValidation.class, "checkOgrn", "304463210700214"));
    }
}
