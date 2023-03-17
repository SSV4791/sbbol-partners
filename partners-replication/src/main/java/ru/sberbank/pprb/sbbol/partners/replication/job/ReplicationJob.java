package ru.sberbank.pprb.sbbol.partners.replication.job;

public interface ReplicationJob {

    ReplicationJobType getJobType();

    void run();
}
