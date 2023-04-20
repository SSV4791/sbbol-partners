package ru.sberbank.pprb.sbbol.partners.fraud;

import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartySendToAnalyzeRq;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.response.AnalyzeResponse;
import ru.sberbank.pprb.sbbol.antifraud.api.exception.AnalyzeException;
import ru.sberbank.pprb.sbbol.antifraud.api.exception.ApplicationException;
import ru.sberbank.pprb.sbbol.antifraud.api.exception.ModelArgumentException;
import ru.sberbank.pprb.sbbol.antifraud.rpc.counterparty.CounterPartyService;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudAdapterException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudApplicationException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudModelArgumentException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudResponseException;

import static java.util.Objects.isNull;

@Loggable
public class FraudAdapterImpl implements FraudAdapter{

    private final CounterPartyService fraudRpcProxy;

    public FraudAdapterImpl(CounterPartyService fraudRpcProxy) {
        this.fraudRpcProxy = fraudRpcProxy;
    }

    @Override
    public AnalyzeResponse send(CounterPartySendToAnalyzeRq rq) {
        try {
            var response = fraudRpcProxy.analyzeOperation(rq);
            if (isNull(response)) {
                throw new FraudResponseException("response is null");
            }
            if (isNull(response.getActionCode())) {
                throw new FraudResponseException("response.actionCode is null");
            }
            return response;
        } catch (ModelArgumentException e) {
            throw new FraudModelArgumentException(e.getMessage(), e);
        } catch (ApplicationException | AnalyzeException e) {
            throw new FraudApplicationException(e.getMessage(), e);
        } catch (Exception e) {
            throw new FraudAdapterException(e.getMessage(), e);
        }
    }
}
