package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.dcbqa.allureee.annotations.layers.ApiTestLayer;
import ru.dcbqa.coverage.swagger.reporter.reporters.RestAssuredCoverageReporter;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.given;

@ApiTestLayer
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@ContextConfiguration(classes =
    {
        PodamConfiguration.class,
        TestReplicationConfiguration.class
    },
    initializers = {
        HibernatePluginCleanerInitializer.class
    }
)
@ExtendWith({SpringExtension.class})
public abstract class AbstractIntegrationTest {

    private static final String BASE_URI = "http://localhost";

    @LocalServerPort
    protected int port;

    protected static RequestSpecification requestSpec;

    protected static ResponseSpecification responseSpec;

    protected static ResponseSpecification createResponseSpec;

    protected static ResponseSpecification createBadRequestResponseSpec;

    protected static ResponseSpecification notContentResponseSpec;

    protected static ResponseSpecification notFoundResponseSpec;

    protected static ResponseSpecification internalServerErrorResponseSpec;

    protected static ResponseSpecification methodNotAllowedResponseSpec;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PodamFactory podamFactory;

    @BeforeAll
    final void setup() {
        initTest();

        requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setPort(port)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .addFilter(new RestAssuredCoverageReporter())
            .addFilter(new AllureRestAssured())
            .addFilter(new ResponseLoggingFilter())
            .log(LogDetail.ALL)
            .build();

        responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.OK.value())
            .build();

        createResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.CREATED.value())
            .build();

        createBadRequestResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.BAD_REQUEST.value())
            .build();

        notContentResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.NO_CONTENT.value())
            .build();

        notFoundResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.NOT_FOUND.value())
            .build();

        internalServerErrorResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();

        methodNotAllowedResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
            .build();
    }

    /**
     * Метод, позволяющий выполнить дополнительные действия по инициализации теста
     */
    protected void initTest() {
    }

    public static <T> T get(String url, HttpStatus responseHttpStatus, Class<T> response, Object... params) {
        return get(url, null, responseHttpStatus, response, params);
    }

    public static <T> T get(String url, Header header, HttpStatus responseHttpStatus, Class<T> response, Object... params) {
        var specification = given()
            .spec(requestSpec)
            .when();
        if (header != null) {
            specification
                .header(header);
        }
        return specification.get(url, params)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .as(response)
            ;
    }

    public static <T, BODY> T post(String url, HttpStatus responseHttpStatus, BODY body, Class<T> response) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .as(response);
    }

    public static <T, BODY> T post(String url, HttpStatus responseHttpStatus, BODY body, Map<String, ?> headers, Class<T> response) {
        return given()
            .spec(requestSpec)
            .headers(headers)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .as(response);
    }

    public static <T, BODY> T post(String url, BODY body, TypeRef<T> response) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(responseSpec)
            .extract()
            .as(response);
    }

    public static <BODY> Response post(String url, HttpStatus responseHttpStatus, BODY body) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .response();
    }

    public static <BODY> void postWithInternalServerErrorExpected(String url, BODY body) {
        given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(internalServerErrorResponseSpec)
            .extract();
    }

    public static <T, BODY> T put(String url, HttpStatus responseHttpStatus, BODY body, Class<T> response) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .put(url)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .as(response);
    }

    public static Response delete(String url, HttpStatus responseHttpStatus, Object... params) {
        return given()
            .spec(requestSpec)
            .when()
            .delete(url, params)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .response();
    }

    public static Response delete(String url, HttpStatus responseHttpStatus, Map<String, ?> queryParams, Object... params) {
        return given()
            .spec(requestSpec)
            .when()
            .queryParams(queryParams)
            .delete(url, params)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .response();
    }

    public static Response delete(String url, HttpStatus responseHttpStatus, Map<String, ?> queryParams,
                                     Map<String, ?> headers, Object... params) {
        return given()
            .spec(requestSpec)
            .when()
            .queryParams(queryParams)
            .headers(headers)
            .delete(url, params)
            .then()
            .spec(specResponseHandler(responseHttpStatus))
            .extract()
            .response();
    }

    public String getBase64FraudMetaData() throws JsonProcessingException {
        var fraudMetaData = podamFactory.manufacturePojo(FraudMetaData.class);
        var fraudMetaDataValue = objectMapper.writeValueAsString(fraudMetaData);
        var base64FraudMetaData = Base64.getEncoder().encode(fraudMetaDataValue.getBytes(StandardCharsets.UTF_8));
        return new String(base64FraudMetaData);
    }

    public String getBase64InvalidFraudMetaData() throws JsonProcessingException {
        var fraudMetaData = podamFactory.manufacturePojo(FraudMetaData.class);
        fraudMetaData.getClientData().setTerBankNumber(null);
        var fraudMetaDataValue = objectMapper.writeValueAsString(fraudMetaData);
        var base64FraudMetaData = Base64.getEncoder().encode(fraudMetaDataValue.getBytes(StandardCharsets.UTF_8));
        return new String(base64FraudMetaData);
    }

    private static ResponseSpecification specResponseHandler(HttpStatus httpStatus) {
        return switch (httpStatus) {
            case OK -> responseSpec;
            case CREATED -> createResponseSpec;
            case BAD_REQUEST -> createBadRequestResponseSpec;
            case NO_CONTENT -> notContentResponseSpec;
            case NOT_FOUND -> notFoundResponseSpec;
            case INTERNAL_SERVER_ERROR -> internalServerErrorResponseSpec;
            case METHOD_NOT_ALLOWED -> methodNotAllowedResponseSpec;
            default -> throw new IllegalStateException("Unexpected value: " + httpStatus);
        };
    }
}
