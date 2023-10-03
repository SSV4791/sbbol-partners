package ru.sberbank.pprb.sbbol.partners.fraud;

import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.AnalyzeRequest;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.response.FullAnalyzeResponse;

public interface FraudAdapter {
    FullAnalyzeResponse send(AnalyzeRequest rq);
}
