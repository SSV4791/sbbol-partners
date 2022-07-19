package ru.sberbank.pprb.sbbol.partners.service.replication.dto;

import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;

public class AsynchReplicationCounterparty {

    private Operation operation;
    private String digitalId;
    private Counterparty counterparty;
    private CounterpartySignData signData;

    public enum Operation {
        CREATE_COUNTERPARTY, UPDATE_COUNTERPARTY, DELETE_COUNTERPARTY, CREATE_SIGN, DELETE_SIGN
    }

    public AsynchReplicationCounterparty operation(Operation operation) {
        this.operation = operation;
        return this;
    }

    public AsynchReplicationCounterparty digitalId(String digitalId) {
        this.digitalId = digitalId;
        return this;
    }

    public AsynchReplicationCounterparty counterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
        return this;
    }

    public AsynchReplicationCounterparty signData(CounterpartySignData signData) {
        this.signData = signData;
        return this;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }

    public CounterpartySignData getSignData() {
        return signData;
    }

    @Override
    public String toString() {
        return "AsynchReplicationCounterparty{" +
            "operation=" + operation +
            ", digitalId='" + digitalId + '\'' +
            ", counterparty=" + counterparty +
            ", signData=" + signData +
            '}';
    }
}
