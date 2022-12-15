package ru.sberbank.pprb.sbbol.partners.fraud;

import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartySendToAnalyzeRq;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.response.AnalyzeResponse;

public interface FraudAdapter {
    AnalyzeResponse send(CounterPartySendToAnalyzeRq rq);
}
