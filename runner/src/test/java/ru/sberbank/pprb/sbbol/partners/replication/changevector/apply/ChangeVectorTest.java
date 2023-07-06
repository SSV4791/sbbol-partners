package ru.sberbank.pprb.sbbol.partners.replication.changevector.apply;

import com.sbt.pprb.integration.changevector.ChangeSet;
import com.sbt.pprb.integration.changevector.ChangeVector;
import com.sbt.pprb.integration.changevector.serialization.Serializer;
import com.sbt.pprb.integration.changevector.serialization.evo.ChangeVectorSerializer;
import com.sbt.pprb.integration.hibernate.changes.serialization.meta.HibernateMetadataSource;
import com.sbt.pprb.integration.hibernate.changes.transform.EventResolver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.partners.config.DataSourceConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.HibernatePluginCleanerInitializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@UnitTestLayer
@ActiveProfiles("cv")
@Import(DataSourceConfiguration.class)
@ContextConfiguration(
    initializers = {HibernatePluginCleanerInitializer.class}
)
@SpringBootTest
class ChangeVectorTest {

    @Autowired
    private SessionFactoryImplementor sessionFactory;

    @Test
    public void applyVectorsTest() throws IOException {
        HibernateMetadataSource metadataSource = new HibernateMetadataSource(sessionFactory);
        Serializer<ChangeVector> serializer = new ChangeVectorSerializer(metadataSource);

        Files.list(Paths.get("../vectors")).forEach(v -> {
            String vector;
            try {
                vector = Files.readString(v.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            apply(vector, serializer);
        });
    }

    private void apply(String data, Serializer<ChangeVector> serializer) {
        ChangeVector changeVector = serializer.deserialize(data);
        SessionImplementor session = sessionFactory.createEntityManager().unwrap(SessionImplementor.class);
        EventResolver resolver = new EventResolver(session, false);

        for (ChangeSet changeSet : changeVector.getChangeSets()) {
            resolver.getContext().fetch(changeSet);
            changeSet.toStream()
                .collect(Collectors.toList())
                .forEach(resolver::resolve);
        }
    }

}
