package ru.sberbank.pprb.sbbol.partners.fraud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartySendToAnalyzeRq;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.response.AnalyzeResponse;
import ru.sberbank.pprb.sbbol.antifraud.api.exception.AnalyzeException;
import ru.sberbank.pprb.sbbol.antifraud.api.exception.ApplicationException;
import ru.sberbank.pprb.sbbol.antifraud.api.exception.ModelArgumentException;
import ru.sberbank.pprb.sbbol.antifraud.rpc.counterparty.CounterPartyService;
import ru.sberbank.pprb.sbbol.partners.fraud.config.FraudProperties;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudAdapterException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudApplicationException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudModelArgumentException;

public class FraudAdapterImpl implements FraudAdapter{

    private static final Logger LOG = LoggerFactory.getLogger(FraudAdapterImpl.class);

    private final CounterPartyService fraudRpcProxy;

    private final FraudProperties fraudProperties;

    public FraudAdapterImpl(
        CounterPartyService fraudRpcProxy,
        FraudProperties fraudProperties
    ) {
        this.fraudRpcProxy = fraudRpcProxy;
        this.fraudProperties = fraudProperties;
    }

    @Override
    public AnalyzeResponse send(CounterPartySendToAnalyzeRq rq) {
        if (fraudRpcProxy == null) {
            LOG.error("Не создан бин fraudJsonRpcProxy. Настройки интеграции с ППРБ Фабрика Агрегатор данных ФРОД-мониторинга: {}", fraudProperties);
            throw new FraudAdapterException("Не создан бин fraudJsonRpcProxy.");
        }
        try {
            return fraudRpcProxy.analyzeOperation(rq);
        } catch (ModelArgumentException e) {
            throw new FraudModelArgumentException(e.getMessage(), e);
        } catch (ApplicationException | AnalyzeException e) {
            throw new FraudApplicationException(e.getMessage(), e);
        } catch (Exception e) {
            throw new FraudAdapterException(e.getMessage(), e);
        }
    }
}
