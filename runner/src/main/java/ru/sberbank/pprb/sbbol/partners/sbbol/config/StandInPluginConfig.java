package ru.sberbank.pprb.sbbol.partners.sbbol.config;

import com.sbt.pprb.integration.changevector.serialization.SerializerType;
import com.sbt.pprb.integration.replication.ReplicationStrategy;
import com.sbt.pprb.integration.replication.hashkey.InterfaceBasedHashKeyResolver;
import com.sbt.pprb.integration.replication.hashkey.StaticHashKeyResolver;
import com.sbt.pprb.integration.replication.journal.JournalHashKeyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// TODO DCBBRAIN-2268 Вынести конфигурацию из модуля Runner в отдельный модуль
@Component
@ConfigurationProperties(prefix = "standin.plugin.configuration")
public class StandInPluginConfig {

    private Resolver journalHashKeyResolver;

    private ReplicationStrategy replicationStrategy;

    private SerializerType serializerType;

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

    public enum Resolver {
        STATIC {
            @Override
            public JournalHashKeyResolver instance() {
                return new StaticHashKeyResolver("1");
            }
        },
        INTERFACE {
            @Override
            public JournalHashKeyResolver instance() {
                return new InterfaceBasedHashKeyResolver();
            }
        };

        public abstract JournalHashKeyResolver instance();
    }

}
