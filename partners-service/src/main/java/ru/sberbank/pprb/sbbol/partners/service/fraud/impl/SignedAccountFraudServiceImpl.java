package ru.sberbank.pprb.sbbol.partners.service.fraud.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.exception.FraudDeniedException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.FraudModelValidationException;
import ru.sberbank.pprb.sbbol.partners.fraud.FraudAdapter;
import ru.sberbank.pprb.sbbol.partners.fraud.config.FraudProperties;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudAdapterException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudApplicationException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudModelArgumentException;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudService;

import static java.util.Objects.isNull;

public class SignedAccountFraudServiceImpl implements FraudService<AccountEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(SignedAccountFraudServiceImpl.class);

    private final String ANALYZE_RESPONSE_ACTION_CODE_ALLOW = "ALLOW";

    protected final FraudProperties properties;

    protected final FraudAdapter adapter;

    protected final SignedAccountFraudMetaDataMapper fraudMapper;

    private final PartnerRepository partnerRepository;

    public SignedAccountFraudServiceImpl(
        FraudProperties properties,
        FraudAdapter adapter,
        SignedAccountFraudMetaDataMapper fraudMapper,
        PartnerRepository partnerRepository
    ) {
        this.properties = properties;
        this.adapter = adapter;
        this.fraudMapper = fraudMapper;
        this.partnerRepository = partnerRepository;
    }

    @Override
    public FraudEventType getEventType() {
        return FraudEventType.SIGN_ACCOUNT;
    }

    @Override
    public void sendEvent(FraudMetaData metaData, AccountEntity accountEntity) {
        if (!properties.isEnabled() || isNull(metaData)) {
            return;
        }
        var partnerEntity = partnerRepository.getByDigitalIdAndUuid(accountEntity.getDigitalId(), accountEntity.getPartnerUuid())
            .orElseThrow(() -> new EntryNotFoundException("partner", accountEntity.getDigitalId(), accountEntity.getPartnerUuid()));
        var request = fraudMapper.mapToCounterPartySendToAnalyzeRq(metaData, partnerEntity, accountEntity);
        try {
            var response = adapter.send(request);
            if (!ANALYZE_RESPONSE_ACTION_CODE_ALLOW.equalsIgnoreCase(response.getActionCode())) {
                throw new FraudDeniedException(response.getComment());
            }
        } catch (FraudModelArgumentException e) {
            throw new FraudModelValidationException(e.getMessage(), e);
        } catch (FraudAdapterException | FraudApplicationException e) {
            LOG.error("Ошибка адаптера АС Интегратор с ФМ ЮЛ: {}", e.getLocalizedMessage());
        }
    }
}
