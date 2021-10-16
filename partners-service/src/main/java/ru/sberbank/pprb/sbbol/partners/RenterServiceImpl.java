package ru.sberbank.pprb.sbbol.partners;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.mapper.RenterMapper;
import ru.sberbank.pprb.sbbol.partners.renter.model.CheckResult;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;
import ru.sberbank.pprb.sbbol.partners.repository.RenterRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RenterServiceImpl implements RenterService {

    private final RenterRepository repository;
    private final ValidationService validationService;
    private final RenterMapper renterMapper;

    public RenterServiceImpl(RenterRepository repository, ValidationService validationService, RenterMapper renterMapper) {
        this.repository = repository;
        this.validationService = validationService;
        this.renterMapper = renterMapper;
    }

    @Override
    public RenterListResponse getRenters(@Nonnull RenterFilter renterFilter) {
        var pagination = renterFilter.getPagination();
        Iterable<ru.sberbank.pprb.sbbol.partners.entity.Renter> renters;
        if (pagination == null) {
            renters = repository.findAllByDigitalId(renterFilter.getDigitalId(), Sort.by("digitalId"));
        } else {
            renters = repository.findAllByDigitalId(renterFilter.getDigitalId(), PageRequest.of(
                pagination.getOffset(),
                pagination.getCount(),
                Sort.by("digitalId")));
        }
        var contracts = new ArrayList<Renter>();
        for (ru.sberbank.pprb.sbbol.partners.entity.Renter renter : renters) {
            contracts.add(renterMapper.toRenter(renter));
        }
        return new RenterListResponse().items(contracts);
    }

    @Override
    public Renter createRenter(@Nonnull Renter renter) {
        List<CheckResult> checkResults = validationService.check(renter);
        if (checkResults.isEmpty()) {
            var uuid = UUID.randomUUID().toString();
            var createRenter = renterMapper.toRenter(renter);
            createRenter.setUuid(uuid);
            if (renter.getPhysicalAddress() != null) {
                createRenter.getPhysicalAddress().setRenter(createRenter);
            }
            if (renter.getLegalAddress() != null) {
                createRenter.getLegalAddress().setRenter(createRenter);
            }
            var save = repository.save(createRenter);
            return renterMapper.toRenter(save);
        } else {
            var result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    @Override
    public Renter updateRenter(@Nonnull Renter renter) {
        List<CheckResult> checkResults = validationService.check(renter);
        if (checkResults.isEmpty()) {
            var renters = repository.findAllByUuid(renter.getUuid());
            if (renters.size() != 1) {
                throw new RuntimeException("Запись не найдена или записей больше 1");
            }
            var targetRenter = renters.get(0);
            renterMapper.updateRenter(renter, targetRenter);
            if (targetRenter.getPhysicalAddress() != null) {
                targetRenter.getPhysicalAddress().setRenter(targetRenter);
            }
            if (targetRenter.getLegalAddress() != null) {
                targetRenter.getLegalAddress().setRenter(targetRenter);
            }
            var saveRanter = repository.save(targetRenter);
            return renterMapper.toRenter(saveRanter);
        } else {
            Renter result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    @Override
    public Renter getRenter(@Nonnull RenterIdentifier renterIdentifier) {
        var renters = repository.findAllByUuidAndDigitalId(renterIdentifier.getUuid(), renterIdentifier.getDigitalId());
        if (renters.isEmpty()) {
            return null;
        } else if (renters.size() > 1) {
            throw new IndexOutOfBoundsException("Найдено больше одного Партнера");
        }
        return renterMapper.toRenter(renters.get(0));
    }
}
