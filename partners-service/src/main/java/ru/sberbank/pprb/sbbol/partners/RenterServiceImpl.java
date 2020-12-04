package ru.sberbank.pprb.sbbol.partners;

import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;

import javax.annotation.Nonnull;

@Service
public class RenterServiceImpl implements RenterService {

    private final RenterDao renterDao;

    public RenterServiceImpl(RenterDao renterDao) {
        this.renterDao = renterDao;
    }

    private static String phoneNumbers = "+79991112233";
    private static String emails = "roga@mail.ru";

    @Override
    public RenterListResponse getRenters(@Nonnull RenterFilter renterFilter) {
        return renterDao.getRenters(renterFilter);
    }

    @Override
    public Renter createRenter(@Nonnull Renter renter) {
        return renterDao.createRenter(renter);
    }

    @Override
    public Renter updateRenter(@Nonnull Renter renter) {
        return renterDao.updateRenter(renter);
    }

    @Override
    public Renter getRenter(@Nonnull String renterGuid) {
        return renterDao.getRenter(renterGuid);
    }
}
