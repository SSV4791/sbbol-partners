package ru.sberbank.pprb.sbbol.partners.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import uk.co.jemos.podam.api.PodamFactory;

@UnitTestLayer
@ContextConfiguration(
    classes = {
        PodamConfiguration.class
    }
)
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
public abstract class BaseUnitConfiguration {

    @Autowired
    protected PodamFactory factory;
}
