package ru.sberbank.pprb.sbbol.partners.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
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
import ru.dcbqa.allureee.annotations.layers.ApiTestLayer;
import ru.dcbqa.coverage.swagger.reporter.reporters.RestAssuredCoverageReporter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.Runner;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static io.restassured.RestAssured.given;

@ApiTestLayer
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = {Runner.class}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@ActiveProfiles("test")
@Import({TestReplicationConfiguration.class})
@ExtendWith(SpringExtension.class)
public abstract class AbstractIntegrationTest {

    private static final String BASE_URI = "http://localhost";

    protected static final PodamFactory factory = new PodamFactoryImpl();

    @LocalServerPort
    protected int port;

    protected static RequestSpecification requestSpec;

    protected static ResponseSpecification responseSpec;

    protected static ResponseSpecification createResponseSpec;

    protected static ResponseSpecification createBadRequestResponseSpec;

    protected static ResponseSpecification notContentResponseSpec;

    protected static ResponseSpecification notFoundResponseSpec;

    protected static ResponseSpecification internalServerErrorResponseSpec;

    @BeforeAll
    public final void setup() {
        initTest();

        requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setPort(port)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .addFilter(new RestAssuredCoverageReporter())
            .addFilter(new AllureRestAssured())
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
    }

    /**
     * Метод, позволяющий выполнить дополнительные действия по инициализации теста
     */
    protected void initTest() {
    }

    protected static <T> T get(String url, Class<T> response, Object... params) {
        return given()
            .spec(requestSpec)
            .when()
            .get(url, params)
            .then()
            .spec(responseSpec)
            .extract()
            .as(response);
    }

    protected static <T> T getNotFound(String url, Class<T> response, Object... params) {
        return given()
            .spec(requestSpec)
            .when()
            .get(url, params)
            .then()
            .spec(notFoundResponseSpec)
            .extract()
            .as(response);
    }

    protected static <T, BODY> T createPost(String url, BODY body, Class<T> response) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(createResponseSpec)
            .extract()
            .as(response);
    }

    protected static <BODY> Error createBadRequestPost(String url, BODY body) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
    }

    protected static <T, BODY> T post(String url, BODY body, Class<T> response) {
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

    protected static <T, BODY> T post(String url, BODY body, TypeRef<T> response) {
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

    protected static <BODY> void postWithInternalServerErrorExpected(String url, BODY body) {
        given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(url)
            .then()
            .spec(internalServerErrorResponseSpec)
            .extract();
    }

    protected static <T, BODY> T put(String url, BODY body, Class<T> response) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .put(url)
            .then()
            .spec(responseSpec)
            .extract()
            .as(response);
    }

    protected static <BODY> Error createBadRequestPut(String url, BODY body) {
        return given()
            .spec(requestSpec)
            .body(body)
            .when()
            .put(url)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
    }

    protected static ResponseBody<?> delete(String url, Object... params) {
        return given()
            .spec(requestSpec)
            .when()
            .delete(url, params)
            .then()
            .spec(notContentResponseSpec)
            .extract()
            .response().getBody();
    }
}
