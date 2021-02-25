package ru.sberbank.pprb.sbbol.partners;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import sbp.com.sbt.dataspace.core.local.runner.junit5.JUnit5DataSpaceCoreLocalRunnerExtension;

@ExtendWith({JUnit5DataSpaceCoreLocalRunnerExtension.class})
@TestPropertySource(properties = {"ceph.required=false", "dataspace-core.model.packagesToScan=ru.sberbank.pprb.sbbol.partners",
        "sbbol.url.root=http://localhost:8081","sbbol.url.replication=/"})
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
@EnableWebMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseControllerTest {

    protected static MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    private void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

}
