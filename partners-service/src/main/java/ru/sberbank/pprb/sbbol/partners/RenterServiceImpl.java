package ru.sberbank.pprb.sbbol.partners;

import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.renter.model.CheckResult;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;

import javax.annotation.Nonnull;
import java.util.List;

@Service
public class RenterServiceImpl implements RenterService {

    private final RenterDao renterDao;
    private final ValidationService validationService;

    public RenterServiceImpl(RenterDao renterDao, ValidationService validationService) {
        this.renterDao = renterDao;
        this.validationService = validationService;
    }

    private static String phoneNumbers = "+79991112233";
    private static String emails = "roga@mail.ru";

    @Override
    public RenterListResponse getRenters(@Nonnull RenterFilter renterFilter) {
        return renterDao.getRenters(renterFilter);
    }

    @Override
    public Renter createRenter(@Nonnull Renter renter) {
        List<CheckResult> checkResults = validationService.check(renter);
        if (checkResults.isEmpty()) {
            return renterDao.createRenter(renter);
        } else {
            Renter result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    @Override
    public Renter updateRenter(@Nonnull Renter renter) {
        List<CheckResult> checkResults = validationService.check(renter);
        if (checkResults.isEmpty()) {
            return renterDao.updateRenter(renter);
        } else {
            Renter result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    @Override
    public Renter getRenter(@Nonnull RenterIdentifier renterIdentifier) {
        return renterDao.getRenter(renterIdentifier.getUuid(), renterIdentifier.getDigitalId());
    }
}
