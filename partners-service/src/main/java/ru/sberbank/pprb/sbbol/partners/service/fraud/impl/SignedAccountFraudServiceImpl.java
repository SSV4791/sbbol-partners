package ru.sberbank.pprb.sbbol.partners.service.fraud.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.FraudDeniedException;
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

import java.util.List;
import java.util.Locale;

import static java.util.Objects.isNull;

@Loggable
public class SignedAccountFraudServiceImpl implements FraudService<AccountEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(SignedAccountFraudServiceImpl.class);

    private static final String ANALYZE_RESPONSE_ACTION_CODE_REVIEW = "REVIEW";

    private static final String ANALYZE_RESPONSE_ACTION_CODE_DENY = "DENY";

    protected final FraudProperties properties;

    protected final FraudAdapter adapter;

    protected final SignedAccountFraudMetaDataMapper fraudMapper;

    private final PartnerRepository partnerRepository;

    private final List<String> forbiddenFraudActionCodes = List.of(
        ANALYZE_RESPONSE_ACTION_CODE_DENY,
        ANALYZE_RESPONSE_ACTION_CODE_REVIEW
    );

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
        if (!properties.isEnabled() || isNull(metaData) || !checkEvent(metaData)) {
            return;
        }
        var partnerEntity = partnerRepository.getByDigitalIdAndUuid(accountEntity.getDigitalId(), accountEntity.getPartnerUuid())
            .orElseThrow(() -> new EntryNotFoundException("partner", accountEntity.getDigitalId(), accountEntity.getPartnerUuid()));
        var request = fraudMapper.mapToCounterPartySendToAnalyzeRq(metaData, partnerEntity, accountEntity);
        try {
            LOG.debug("Отправляем запрос В АС Агрегатор данных ФРОД-мониторинг: {}", request);
            var response = adapter.send(request);
            LOG.debug("Получили ответ от АС Агрегатора данных ФРОД-мониторинг: {}", response);
            if (forbiddenFraudActionCodes.contains(response.getActionCode().toUpperCase(Locale.getDefault()))) {
                throw new FraudDeniedException(response.getDetailledComment());
            }
        } catch (FraudModelArgumentException e) {
            throw new FraudModelValidationException(e.getMessage(), e);
        } catch (FraudAdapterException | FraudApplicationException e) {
            LOG.error("Ошибка адаптера АС Интегратор с ФМ ЮЛ: {}", ExceptionUtils.getStackTrace(e));
        }
    }
}
