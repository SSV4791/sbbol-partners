package ru.sberbank.pprb.sbbol.partners.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.service.partner.RenterAccountUpdaterService;

import java.util.List;

@Loggable
public class RenterAccountUpdaterJob {

    private static final Logger LOG = LoggerFactory.getLogger(RenterAccountUpdaterJob.class);

    private final RenterAccountUpdaterService renterAccountUpdaterService;

    public RenterAccountUpdaterJob(RenterAccountUpdaterService renterAccountUpdaterService) {
        this.renterAccountUpdaterService = renterAccountUpdaterService;
    }

    public void run(int sleepTime, int batchSize) {
        LOG.info("Процедура обновления типа счетов рентеров запущена");
        var offset = 0;
        while (true) {
            var pagination = new Pagination()
                .offset(offset)
                .count(batchSize);
            List<PartnerEntity> renters = renterAccountUpdaterService.getRenters(pagination);
            if (CollectionUtils.isEmpty(renters)) {
                break;
            }
            renters.forEach(renterAccountUpdaterService::updateAccountType);
            offset = offset + renters.size();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOG.warn("Interrupted!", e);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }
        LOG.info("Процедура обновления типа счетов рентеров закончена");
    }
}
