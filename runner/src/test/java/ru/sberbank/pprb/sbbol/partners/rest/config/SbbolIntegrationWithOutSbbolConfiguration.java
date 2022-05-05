package ru.sberbank.pprb.sbbol.partners.rest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;

import javax.annotation.PostConstruct;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
public class SbbolIntegrationWithOutSbbolConfiguration {

    @MockBean(reset = MockReset.NONE)
    private LegacySbbolAdapter legacySbbolAdapter;

    @PostConstruct
    void init() {
        when(legacySbbolAdapter.checkNotMigration(any())).thenReturn(false);
    }
}
