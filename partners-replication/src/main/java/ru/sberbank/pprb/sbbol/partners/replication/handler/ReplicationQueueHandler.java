package ru.sberbank.pprb.sbbol.partners.replication.handler;

public interface ReplicationQueueHandler {

    void handle(int retry);
}
