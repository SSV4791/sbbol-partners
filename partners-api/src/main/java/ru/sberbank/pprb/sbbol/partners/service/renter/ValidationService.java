package ru.sberbank.pprb.sbbol.partners.service.renter;

import ru.sberbank.pprb.sbbol.partners.renter.model.CheckResult;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;

import java.util.List;

public interface ValidationService {

    /**
     * Валидация арендатора
     *
     * @param renter данные арендатора
     * @return результат валиации
     */
    List<CheckResult> check(Renter renter);

}
