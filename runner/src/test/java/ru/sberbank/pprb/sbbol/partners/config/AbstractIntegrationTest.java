package ru.sberbank.pprb.sbbol.partners.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.Runner;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {Runner.class}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@ActiveProfiles("test")
@Import({TestReplicationConfiguration.class})
@ExtendWith(SpringExtension.class)
public abstract class AbstractIntegrationTest {

    private static final String BASE_URI = "http://localhost";

    @LocalServerPort
    protected int port;

    protected RequestSpecification requestSpec;

    protected ResponseSpecification responseSpec;

    protected ResponseSpecification badRequestResponseSpec;

    protected ResponseSpecification internalServerErrorResponseSpec;

    @BeforeAll
    public final void setup() {
        initTest();

        requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setPort(port)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

        responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.OK.value())
            .build();

        internalServerErrorResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();

        badRequestResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.BAD_REQUEST.value())
            .build();
    }

    /**
     * Метод, позволяющий выполнить дополнительные действия по инициализации теста
     */
    protected void initTest() {
    }
}
