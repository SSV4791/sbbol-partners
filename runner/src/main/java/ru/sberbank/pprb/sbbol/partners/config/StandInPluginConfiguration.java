package ru.sberbank.pprb.sbbol.partners.config;

import com.sbt.pprb.integration.changevector.serialization.SerializerType;
import com.sbt.pprb.integration.replication.OrderingControlStrategy;
import com.sbt.pprb.integration.replication.PartitionLockMode;
import com.sbt.pprb.integration.replication.PartitionMultiplyingMode;
import com.sbt.pprb.integration.replication.ReplicationStrategy;
import com.sbt.pprb.integration.replication.hashkey.InterfaceBasedHashKeyResolver;
import com.sbt.pprb.integration.replication.hashkey.StaticHashKeyResolver;
import com.sbt.pprb.integration.replication.journal.JournalHashKeyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "standin.plugin.configuration")
public class StandInPluginConfiguration {

    private Resolver journalHashKeyResolver;

    private ReplicationStrategy replicationStrategy;

    private SerializerType serializerType;

    private PartitionLockMode partitionLockMode;

    private OrderingControlStrategy orderingControlStrategy;

    private PartitionMultiplyingMode partitionMultiplyingMode;

    public Resolver getJournalHashKeyResolver() {
        return journalHashKeyResolver;
    }

    public void setJournalHashKeyResolver(Resolver journalHashKeyResolver) {
        this.journalHashKeyResolver = journalHashKeyResolver;
    }

    public ReplicationStrategy getReplicationStrategy() {
        return replicationStrategy;
    }

    public void setReplicationStrategy(ReplicationStrategy replicationStrategy) {
        this.replicationStrategy = replicationStrategy;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public void setSerializerType(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    public PartitionLockMode getPartitionLockMode() {
        return partitionLockMode;
    }

    public void setPartitionLockMode(PartitionLockMode partitionLockMode) {
        this.partitionLockMode = partitionLockMode;
    }

    public OrderingControlStrategy getOrderingControlStrategy() {
        return orderingControlStrategy;
    }

    public void setOrderingControlStrategy(OrderingControlStrategy orderingControlStrategy) {
        this.orderingControlStrategy = orderingControlStrategy;
    }

    public PartitionMultiplyingMode getPartitionMultiplyingMode() {
        return partitionMultiplyingMode;
    }

    public void setPartitionMultiplyingMode(PartitionMultiplyingMode partitionMultiplyingMode) {
        this.partitionMultiplyingMode = partitionMultiplyingMode;
    }

    public enum Resolver {
        STATIC {
            @Override
            public JournalHashKeyResolver instance() {
                return new StaticHashKeyResolver("STATIC");
            }
        },
        INTERFACE {
            @Override
            public JournalHashKeyResolver instance() {
                return new InterfaceBasedHashKeyResolver();
            }
        }
        ;

        public abstract JournalHashKeyResolver instance();
    }

}
