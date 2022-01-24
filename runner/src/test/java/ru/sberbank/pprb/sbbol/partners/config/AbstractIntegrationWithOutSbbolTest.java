package ru.sberbank.pprb.sbbol.partners.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractIntegrationWithOutSbbolTest extends AbstractIntegrationTest {

    @MockBean
    private LegacySbbolAdapter legacySbbolAdapter;

    @BeforeEach
    void init() {
        when(legacySbbolAdapter.checkMigration(any())).thenReturn(true);
    }

}
