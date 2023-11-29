package ru.sberbank.pprb.sbbol.partners.rest.partner;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFilterType;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerInfo;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.SearchPartners;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;
import ru.sberbank.pprb.sbbol.partners.rest.partner.provider.PartnerCreateArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.rest.partner.provider.PartnerFilterIdsArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.rest.partner.provider.PartnerFilterLegalFormArgumentsProvider;
import ru.sberbank.pprb.sbbol.renter.model.Renter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getBic;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidAccountNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidCorrAccountNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidInnNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidOgrnNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidOkpoNumber;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.ACCOUNT_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.FRAUD_MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.PARTNER_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.model.Error.TypeEnum.BUSINESS;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidBudgetAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountControllerTest.createValidBudgetAccountWith40101Balance;
import static ru.sberbank.pprb.sbbol.partners.rest.renter.RenterUtils.getValidRenter;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class PartnerControllerTest extends AbstractIntegrationTest {

    public static final String BASE_ROUTE_PATH = "/partner";

    public static final String PARTNER_GETTING_URL = "/partners/{digitalId}/{id}";

    public static final String FULL_MODEL_PARTNER_PATCH_URL = BASE_ROUTE_PATH + "/full-model/patch";

    @Autowired
    private GkuInnDictionaryRepository innDictionaryRepository;

    private GkuInnEntity gkuInnEntity1;
    private GkuInnEntity gkuInnEntity2;

    @AfterEach
    void after() {
        if (gkuInnEntity1 != null) {
            innDictionaryRepository.delete(gkuInnEntity1);
        }
        if (gkuInnEntity2 != null) {
            innDictionaryRepository.delete(gkuInnEntity2);
        }
    }

    @Test
    void testCreatePartnerWithoutDigitalId() {
        var partner = getValidLegalEntityPartner("");
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        List<Descriptions> descriptions = error.getDescriptions();
        assertThat(descriptions)
            .isNotNull()
            .size()
            .isEqualTo(1);
        Descriptions description = descriptions.stream()
            .filter(value -> value.getField().equals("digitalId"))
            .findFirst().orElse(null);
        assertThat(description).isNotNull();
        assertThat(description.getMessage()).contains("Поле обязательно для заполнения");
        assertThat(description.getMessage()).contains("размер должен находиться в диапазоне от 1 до 40");
        assertThat(description.getMessage()).size().isEqualTo(2);
    }

    @ParameterizedTest
    @ArgumentsSource(PartnerCreateArgumentsProvider.class)
    @DisplayName("POST /partner Создание партнера")
    void testCreatePartner(PartnerCreate partner) {
        assertThat(partner)
            .isNotNull();
        var createdPartner = step("Подготовка тестовых данных", () -> post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partner,
            Partner.class
        ));
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = step("Выполнение post-запроса /partner, код ответа 200", () -> get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(actualPartner)
                .isNotNull();
            assertThat(actualPartner)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("changeDate")
                .isEqualTo(createdPartner);
        });
    }

    @Test
    void testCreatePartnerWithoutLegalForm() {
        var partner = getValidLegalEntityPartner(randomAlphabetic(10))
            .legalForm(null);
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        List<Descriptions> descriptions = error.getDescriptions();
        assertThat(descriptions)
            .isNotNull()
            .size()
            .isEqualTo(1);
        Descriptions description = descriptions.stream()
            .filter(value -> value.getField().equals("legalForm"))
            .findFirst().orElse(null);
        assertThat(description).isNotNull();
        assertThat(description.getMessage()).contains("Поле обязательно для заполнения");
        assertThat(description.getMessage()).size().isEqualTo(1);
    }

    @Test
    void testGetOnePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testCreateContact2() {
        var partner = getValidLegalEntityPartner(randomAlphabetic(10));
        partner.setEmails(null);
        partner.setPhones(null);
        var createPartner = createValidPartner(partner);
        assertThat(createPartner)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "phones",
                "emails"
            )
            .isEqualTo(createPartner);
    }


    @Test
    void testGetSearchInnPartners() {
        var digitalId = randomAlphabetic(10);
        var innForPartner = getValidInnNumber(LegalForm.LEGAL_ENTITY);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId)
                .inn(innForPartner),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId)
                .inn(innForPartner),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getInn().substring(4))
        );
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchBudgetPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidBudgetAccount(createdPartner1.getId(), createdPartner1.getDigitalId());

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.BUDGET);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGettingSearchBudgetPartners() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidBudgetAccountWith40101Balance(partner.getId(), partner.getDigitalId());

            return new PartnersFilter()
                .digitalId(partner.getDigitalId())
                .partnersFilter(PartnerFilterType.BUDGET)
                .pagination(
                    new Pagination()
                        .offset(0)
                        .count(4)
                );
        });
        var partnersResponse = step("Выполнение post-запроса /partners/view, код ответа 200", () -> post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(partnersResponse)
                .isNotNull();
            assertThat(partnersResponse.getPartners()).asList()
                .hasSize(1);
        });
    }

    @Test
    void testGettingSearchBudgetPartners_whenPartnerIsCreatingByFullModelAndBankAccountIsNull() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = getValidFullModelLegalEntityPartner();
            var account = new AccountCreateFullModel()
                .account("40101810822805200005")
                .bank(
                    new BankCreate()
                        .name("Банк")
                        .bic("044525000")
                );
            partner.setAccounts(Set.of(account));

            var createdPartner = post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partner,
                PartnerFullModelResponse.class
            );

            return new PartnersFilter()
                .digitalId(createdPartner.getDigitalId())
                .partnersFilter(PartnerFilterType.BUDGET)
                .pagination(
                    new Pagination()
                        .offset(0)
                        .count(4)
                );
        });
        var partnersResponse = step("Выполнение post-запроса /partners/view, код ответа 200", () -> post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(partnersResponse)
                .isNotNull();
            assertThat(partnersResponse.getPartners()).asList()
                .hasSize(1);
        });
    }

    @Test
    void testGetSearchGKUPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidBudgetAccount(createdPartner1.getId(), createdPartner1.getDigitalId());
        gkuInnEntity1 = new GkuInnEntity();
        gkuInnEntity1.setInn(createdPartner1.getInn());
        innDictionaryRepository.save(gkuInnEntity1);
        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        gkuInnEntity2 = new GkuInnEntity();
        gkuInnEntity2.setInn(createdPartner2.getInn());
        innDictionaryRepository.save(gkuInnEntity2);

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.GKU);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchOrgNamePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getOrgName().substring(4))
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchOrgNamePartnersWithQuotesAndSpaceSymbol() {
        var digitalId = randomAlphabetic(10);
        var orgName = "ОАО \"Ромашка\"";
        var createPartner = getValidLegalEntityPartner(digitalId);
        createPartner.setOrgName(orgName);
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            createPartner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(orgName)
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );
        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchFIOPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidPhysicalPersonPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getSecondName() + " " + createdPartner1.getFirstName())
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchByOrgName_whenDifferentWordCase() {
        var digitalId = randomAlphabetic(10);
        var partnerCreate = getValidLegalEntityPartner(digitalId);
        partnerCreate.setOrgName(partnerCreate.getOrgName().toLowerCase());
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        partnerCreate.setOrgName(partnerCreate.getOrgName().toUpperCase());
        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(partnerCreate.getOrgName())
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners())
            .hasSize(2);
    }

    @Test
    void testGetSearchLegalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,

            getValidPhysicalPersonPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.LEGAL_ENTITY);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchPhysicalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidPhysicalPersonPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.PHYSICAL_PERSON);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchEntrepreneurPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.ENTREPRENEUR);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetPartnersNotSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidAccount(createdPartner1.getId(), createdPartner1.getDigitalId());

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.accountSignType(SignType.NOT_SIGNED);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetPartnersSignedAccount() throws JsonProcessingException {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        var validAccount = createValidAccount(createdPartner1.getId(), createdPartner1.getDigitalId());
        createValidAccountsSign(createdPartner1.getDigitalId(), validAccount.getId(), validAccount.getVersion(), getBase64FraudMetaData());

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.accountSignType(SignType.SIGNED);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @ParameterizedTest
    @ArgumentsSource(PartnerFilterLegalFormArgumentsProvider.class)
    void testSavePartner_whenLegalFormIsNull(PartnersFilter filter) {
        post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidPartner(filter.getDigitalId(), LegalForm.LEGAL_ENTITY),
            Partner.class
        );
        post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidPartner(filter.getDigitalId(), LegalForm.ENTREPRENEUR),
            Partner.class
        );
        post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidPartner(filter.getDigitalId(), LegalForm.PHYSICAL_PERSON),
            Partner.class
        );
        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        List<PartnerInfo> partners = response.getPartners();
        assertThat(partners)
            .hasSameSizeAs(filter.getLegalForms());
        for (PartnerInfo partner : partners) {
            assertThat(filter.getLegalForms())
                .contains(partner.getLegalForm());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PartnerFilterIdsArgumentsProvider.class)
    void testGetPartners_whenUsePartnerIds(PartnersFilter filter) {
        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        List<PartnerInfo> partners = response.getPartners();
        assertThat(partners)
            .hasSameSizeAs(filter.getIds());
        for (var partner : partners) {
            assertThat(filter.getIds())
                .contains(partner.getId());
        }
    }

    @Test
    void testNegativeUpdateChildPartner() {
        var partner = createValidPartner(randomAlphabetic(10));
        HashSet<Phone> newPhones = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion());
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones.add(newPhone);
            }
        }
        HashSet<Email> newEmails = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion());
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails.add(newEmail);
            }
        }
        partner.setPhones(newPhones);
        partner.setEmails(newEmails);
        var newUpdatePartner = put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        assertThat(newUpdatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getFirstName())
            .isEqualTo(newUpdatePartner.getFirstName());
        assertThat(newUpdatePartner.getFirstName())
            .isNotEqualTo(partner.getFirstName());

        HashSet<Phone> newPhones1 = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones1.add(newPhone);
            }
        }
        HashSet<Email> newEmails1 = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion() + 10);
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails1.add(newEmail);
            }
        }
        partner.setPhones(newPhones1);
        partner.setEmails(newEmails1);
        partner.setVersion(newUpdatePartner.getVersion() + 1);
        var newUpdatePartner1 = put(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            updatePartner(partner),
            Error.class
        );
        assertThat(newUpdatePartner1)
            .isNotNull();
        assertThat(newUpdatePartner1.getCode())
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
    }

    @Test
    void testUpdateChildPartner() {
        var partner = createValidPartner(randomAlphabetic(10));
        HashSet<Phone> newPhones = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion());
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones.add(newPhone);
            }
        }
        HashSet<Email> newEmails = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion());
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails.add(newEmail);
            }
        }
        partner.setPhones(newPhones);
        partner.setEmails(newEmails);
        var newUpdatePartner = put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        assertThat(newUpdatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getFirstName())
            .isEqualTo(newUpdatePartner.getFirstName());
        assertThat(newUpdatePartner.getFirstName())
            .isNotEqualTo(partner.getFirstName());

        Set<Phone> newPhones1 = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones1.add(newPhone);
            }
        }
        Set<Email> newEmails1 = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail1 = new Email();
                newEmail1.setVersion(email.getVersion() + 1);
                newEmail1.setId(email.getId());
                newEmail1.setUnifiedId(email.getUnifiedId());
                newEmail1.setDigitalId(email.getDigitalId());
                newEmail1.setEmail(randomAlphabetic(64) + "@mail.ru");
                newEmails1.add(newEmail1);
            }
        }
        partner.setPhones(newPhones1);
        partner.setEmails(newEmails1);
        partner.setVersion(partner.getVersion() + 1);
        var newUpdatePartner1 = put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        assertThat(newUpdatePartner1)
            .isNotNull();
        assertThat(newUpdatePartner1.getFirstName())
            .isEqualTo(newUpdatePartner1.getFirstName());
        assertThat(newUpdatePartner1.getFirstName())
            .isNotEqualTo(partner.getFirstName());
    }

    @Test
    void testNegativeGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner3)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);

        var response = post(
            "/partners/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter1 = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .count(2));
        var response1 = post(
            "/partners/view",
            HttpStatus.BAD_REQUEST,
            filter1,
            Error.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter2 = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(0));
        var response2 = post(
            "/partners/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner3)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners())
            .hasSize(2);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void testGetPartnersWithoutRenters() {
        var digitalId = randomAlphabetic(10);
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();

        var createdRenter = createValidRenter(digitalId);
        assertThat(createdRenter)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners())
            .hasSize(1);
        assertThat(response.getPartners().get(0).getDigitalId())
            .isEqualTo(digitalId);
        assertThat(response.getPartners().get(0).getId())
            .isEqualTo(createdPartner.getId());
    }

    @Test
    void testCreatePartner() {
        var partner = createValidPartner();
        assertThat(partner)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "emails.unifiedUuid",
                "emails.uuid",
                "phones.unifiedUuid",
                "phones.uuid"
            )
            .isEqualTo(partner);
    }

    @Test
    void testCreatePartner_whenDuplicationByEmptyAndNullKpp() {
        var partner = getValidLegalEntityPartner();
        partner.setKpp(StringUtils.EMPTY);
        createValidPartner(partner);
        partner.setKpp(null);
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(PARTNER_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void testCreatePartner_whenDuplicationByEmptyAndNullInn() {
        var partner = getValidPhysicalPersonPartner();
        partner.setInn(StringUtils.EMPTY);
        createValidPartner(partner);
        partner.setInn(null);
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(PARTNER_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void testCreatePartner_unique() {
        var partner = getValidLegalEntityPartner();
        createValidPartner(partner);
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(PARTNER_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void testCreateNotValidPartner() {
        var error = createNotValidPartner();
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testCreatePartnerEmptyOrgName() {
        var partner = getValidLegalEntityPartner();
        partner.setOrgName("");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле обязательно для заполнения");
    }

    @Test
    void testCreatePartnerInvalidCharsInOrgName() {
        var partner = getValidLegalEntityPartner();
        partner.setOrgName("[Наименование Ёё\\] [§±]");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): §±");
    }

    @Test
    void testUpdatePartner() {
        var partner = getValidLegalEntityPartner();
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        String newKpp = "999999999";
        createdPartner.kpp(newKpp);
        createdPartner.setPhones(null);
        createdPartner.setEmails(null);
        var newUpdatePartner = put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );

        assertThat(createdPartner)
            .isNotNull();
        assertThat(newUpdatePartner.getKpp())
            .isEqualTo(newKpp);
    }

    @Test
    void testUpdatePartner_unique() {
        var partner = getValidLegalEntityPartner();
        String newKpp = "999999999";
        var createdPartner1 = createValidPartner(partner);
        partner.setKpp(newKpp);
        createValidPartner(partner);
        var errorUpdatePartner = createdPartner1.kpp(newKpp);
        var error = put(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            errorUpdatePartner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(PARTNER_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void negativeTestUpdatePartnerVersion() {
        var partner = createValidPartner();
        Long version = partner.getVersion() + 1;
        partner.setVersion(version);
        var partnerError = put(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            updatePartner(partner),
            Error.class
        );
        assertThat(partnerError.getCode())
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(partnerError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (partner.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdatePartnerVersion() {
        var partner = createValidPartner();
        var updatePartner = put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        var checkPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            updatePartner.getDigitalId(),
            updatePartner.getId()
        );
        assertThat(checkPartner)
            .isNotNull();
        assertThat(checkPartner.getVersion())
            .isEqualTo(partner.getVersion() + 1);
    }

    @Test
    void testDeletePartner_whenInvalidFraudMetaDataHeader_thenBadRequest() {
        var uuid = podamFactory.manufacturePojo(UUID.class);
        var digitalId = podamFactory.manufacturePojo(String.class);
        var error = delete(
            "/partners/{digitalId}",
            HttpStatus.BAD_REQUEST,
            Map.of("ids", uuid.toString()),
            Map.of("Fraud-Meta-Data", "invalid-fraud-meta-data"),
            digitalId
        ).getBody().as(Error.class);
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(FRAUD_MODEL_VALIDATION_EXCEPTION.getValue());
        assertThat(error.getType())
            .isEqualTo(BUSINESS);
        assertThat(error.getMessage())
            .isEqualTo(MessagesTranslator.toLocale("fraud.conversion_error_from_http_header"));
    }

    @Test
    void testDeletePartner_whenInvalidFraudMetaData_thenBadRequest() throws JsonProcessingException {
        var uuid = podamFactory.manufacturePojo(UUID.class);
        var digitalId = podamFactory.manufacturePojo(String.class);
        var error = delete(
            "/partners/{digitalId}",
            HttpStatus.BAD_REQUEST,
            Map.of("ids", uuid.toString()),
            Map.of("Fraud-Meta-Data", getBase64InvalidFraudMetaData()),
            digitalId
        ).getBody().as(Error.class);
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        assertThat(error.getType())
            .isEqualTo(BUSINESS);
        assertThat(error.getDescriptions())
            .hasSize(1);
        assertThat(error.getDescriptions().get(0).getField())
            .isEqualTo("fraudMetaData.clientData.digitalId");
        assertThat(error.getDescriptions().get(0).getMessage())
            .contains("Поле обязательно для заполнения");

    }

    @Test
    void testDeprecatedDeletePartner() throws JsonProcessingException {
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(),
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(actualPartner)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("changeDate")
            .isEqualTo(createdPartner);

        delete(
            "/partners/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", actualPartner.getId()),
            Map.of("Fraud-Meta-Data", getBase64FraudMetaData()),
            actualPartner.getDigitalId()
        ).getBody();

        var searchPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.NOT_FOUND,
            Error.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(searchPartner)
            .isNotNull();

        assertThat(searchPartner.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
    void testCreatePartnerWithContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        var expectedPhones = partnerCreate.getPhones();
        var expectedEmails = partnerCreate.getEmails();
        var actualPhones = createdPartner.getPhones()
            .stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var actualEmails = createdPartner.getEmails()
            .stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(actualPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedPhones);
        assertThat(actualEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedEmails);
    }

    @Test
    void testUpdatePartnerContacts_updateExistContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotEmpty();
        assertThat(createdPartner.getEmails())
            .isNotEmpty();
        var updatePhone = createdPartner.getPhones().stream().findFirst().orElse(null);
        assertThat(updatePhone)
            .isNotNull();
        updatePhone.setPhone("0070987654321");
        var updateEmail = createdPartner.getEmails().stream().findFirst().orElse(null);
        assertThat(updateEmail)
            .isNotNull();
        updateEmail.setEmail("12345@mail.ru");
        put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .isNotNull();
        var updatePhones = createdPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var updatedPhones = updatedPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        assertThat(updatedPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updatePhones);
        var updateEmails = createdPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        var updatedEmails = updatedPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(updatedEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updateEmails);
    }

    @Test
    void testUpdatePartnerContacts_addNewContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        createdPartner.getPhones().add(new Phone().phone("0071234567890"));
        createdPartner.getEmails().add(new Email().email("007@mail.ru"));
        put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .isNotNull();
        var updatePhones = createdPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var updatedPhones = updatedPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        assertThat(updatedPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updatePhones);
        var updateEmails = createdPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        var updatedEmails = updatedPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(updatedEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updateEmails);
    }

    @Test
    void testUpdatePartnerContacts_deleteAnyContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        partnerCreate.getPhones().add("0071234567890");
        partnerCreate.getEmails().add("123@mail.ru");
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        var phones = createdPartner.getPhones().stream().skip(1).collect(Collectors.toSet());
        var emails = createdPartner.getEmails().stream().skip(1).collect(Collectors.toSet());
        createdPartner.setPhones(phones);
        createdPartner.setEmails(emails);
        put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .isNotNull();
        var updatePhones = createdPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var updatedPhones = updatedPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        assertThat(updatedPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updatePhones);
        var updateEmails = createdPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        var updatedEmails = updatedPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(updatedEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updateEmails);
    }

    @Test
    void testUpdatePartnerContacts_deleteAllContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        partnerCreate.getPhones().add("0071234567890");
        partnerCreate.getEmails().add("123@mail.ru");
        var createdPartner = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        createdPartner.setPhones(Collections.emptySet());
        createdPartner.setEmails(Collections.emptySet());
        put(
            BASE_ROUTE_PATH,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            PARTNER_GETTING_URL,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(Collections.emptySet());

        assertThat(updatedPartner.getEmails())
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(Collections.emptySet());
    }

    @Test
    void savePartnerFullModel() {
        var request = getValidFullModelLegalEntityPartner();
        var createdPartner = post(
            BASE_ROUTE_PATH + "/full-model",
            HttpStatus.CREATED,
            request,
            PartnerFullModelResponse.class
        );
        assertThat(createdPartner)
            .isNotNull();
    }

    @Test
    void savePartnerFullModel_notValidateCodeCurrencyAndBalance() {
        var request = getValidFullModelLegalEntityPartner();
        AccountCreateFullModel accountCreateFullModel = podamFactory.manufacturePojo(AccountCreateFullModel.class);
        accountCreateFullModel.setAccount("00101643145250000411");
        request.setAccounts(Set.of(
            accountCreateFullModel
        ));
        var error = post(
            BASE_ROUTE_PATH + "/full-model",
            HttpStatus.BAD_REQUEST,
            request,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        List<String> errorsMessage = error.getDescriptions().stream()
            .map(Descriptions::getMessage)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        assertThat(errorsMessage)
            .asList()
            .contains("Единый казначейский счёт должен начинаться с 40102");
    }

    @Test
    void savePartnerFullModel_physicalPersonNotSetBudgetCorrAccount() {
        var request = getValidFullModelLegalEntityPartner()
            .legalForm(LegalForm.PHYSICAL_PERSON);
        AccountCreateFullModel accountCreateFullModel = podamFactory.manufacturePojo(AccountCreateFullModel.class);
        accountCreateFullModel.getBank().getBankAccount().setBankAccount("40102643145250000411");
        request.setAccounts(Set.of(
            accountCreateFullModel
        ));
        var error = post(
            BASE_ROUTE_PATH + "/full-model",
            HttpStatus.BAD_REQUEST,
            request,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        List<String> errorsMessage = error.getDescriptions().stream()
            .map(Descriptions::getMessage)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        assertThat(errorsMessage)
            .contains("Единый казначейский счёт не должен использоваться для физического лица или ИП");
    }

    @Test
    void savePartnerFullModelEmptyOrgName() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.setOrgName("");
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле обязательно для заполнения");
    }

    @Test
    void savePartnerFullModelInvalidOrgName() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.setOrgName("[Наименование Ёё \\] §±`~><");
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): §±");
    }

    @Test
    void savePartnerFullModelInvalidBankName() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.getAccounts().forEach(x -> x.getBank().setName(""));
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotNull.message"));

        var partner1 = getValidFullModelLegalEntityPartner();
        partner1.getAccounts().forEach(x -> x.getBank().setName("Наименование банка [§±]"));
        var error1 = given()
            .spec(requestSpec)
            .body(partner1)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.illegal_symbols") + " [§±]");

        var str = "0123456789";
        var partner2 = getValidFullModelLegalEntityPartner();
        partner2.getAccounts().forEach(x -> x.getBank().setName(str.repeat(17)));
        var error2 = given()
            .spec(requestSpec)
            .body(partner2)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error2.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 160");
    }

    @Test
    void savePhysicalPartnerInvalidName() {
        var partner = getValidPhysicalPersonPartner();
        partner.setFirstName("[Имя Ёё] \\ §±`~><");
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): §±");
    }

    @Test
    void savePhysicalPartnerInvalidComment() {
        var partner = getValidPhysicalPersonPartner();
        partner.setComment("[Коммент Ёё §±]");
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [§±]");

        var str = "0123456789";
        partner.setComment(str.repeat(26));
        var error1 = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 255");
    }

    @Test
    void saveFullModelPartnerInvalidAccountComment() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.getAccounts().forEach(x -> x.setComment("[Comment §± Ёё]"));
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [§±]");

        var str = "0123456789";
        partner.getAccounts().forEach(x -> x.setComment(str.repeat(6)));
        var error1 = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 50");
    }

    @Test
    void testSavePartnerWithInvalidKpp() {
        var partner = getValidLegalEntityPartner();
        partner.setKpp("1234567890");
        var error = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 9");

        partner.setKpp("[АБВ1234]");
        var error1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [АБВ]")
            .contains("Введён неверный КПП");

        partner.setKpp("003456789");
        var error2 = post(
            BASE_ROUTE_PATH,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error2.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Введён неверный КПП");
    }

    @Test
    void testSavePartnerWithNullOrEmptyKpp() {
        var partner = getValidLegalEntityPartner();
        partner.setKpp(null);
        var response = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getKpp())
            .isNull();

        partner.setKpp("0");
        var response1 = post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getKpp())
            .isEqualTo("0");
    }

    @Test
    void testSaveFullModelPartner_whenDuplicatePartner() {
        var partner = getValidFullModelLegalEntityPartner();
        given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createResponseSpec);
        var actualError = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        var expectedError = new Error()
            .code(PARTNER_DUPLICATE_EXCEPTION.getValue())
            .message(MessagesTranslator.toLocale("partner.duplicate"))
            .type(BUSINESS)
            .descriptions(Collections.emptyList());
        assertThat(actualError)
            .isEqualTo(expectedError);
    }

    @Test
    void testSavePartner_whenInvalidOgrnLength() {
        var partner = getValidLegalEntityPartner();
        partner.setOgrn("11");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.legal_entity.ogrn.length"))
            .contains(MessagesTranslator.toLocale("validation.partner.ogrn.control_number"));
    }

    @Test
    void testSavePartner_whenLegalFormIsNull() {
        var partner = getValidLegalEntityPartner();
        partner.setLegalForm(null);
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        var expectedDescription = new Descriptions().field("legalForm").message(List.of("Поле обязательно для заполнения"));
        var expectedDescriptions = List.of(expectedDescription);
        assertThat(error.getDescriptions())
            .usingRecursiveComparison()
            .isEqualTo(expectedDescriptions);
    }

    @Test
    void testSaveFullModelPartner_whenLegalFormIsNull() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.setLegalForm(null);
        partner.setInn(getValidInnNumber(LegalForm.PHYSICAL_PERSON));
        partner.setOkpo(getValidOkpoNumber(LegalForm.PHYSICAL_PERSON));
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        var expectedDescription = new Descriptions().field("legalForm").message(List.of("Поле обязательно для заполнения"));
        var expectedDescriptions = List.of(expectedDescription);
        assertThat(error.getDescriptions())
            .usingRecursiveComparison()
            .isEqualTo(expectedDescriptions);
    }

    @Test
    void testSavePartner_whenInvalidOgrnKey() {
        var partner = getValidLegalEntityPartner();
        partner.setOgrn("1234567890123");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.ogrn.control_number"));
    }

    @Test
    void testSavePartner_whenInvalidOkpoLength() {
        var partner = getValidLegalEntityPartner();
        partner.setOkpo("1234567890123");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.legal_entity.okpo.length"));

        var partner1 = getValidEntrepreneurPartner(randomAlphabetic(10));
        partner1.setOkpo("123");
        var error1 = given()
            .spec(requestSpec)
            .body(partner1)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.entrepreneur.okpo.length"));
    }

    @Test
    void testSavePartner_whenInvalidCharsOkpo() {
        var partner = getValidLegalEntityPartner();
        partner.setOkpo("12345АБВ");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(BASE_ROUTE_PATH)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): АБВ");
    }

    @Test
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Дубль по основным реквизитам")
    void testUpdatePartnerFullModel_changePartnerAttributesDouble() {
        var createdFullModelPartner = step("Создание первого Партнера", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class);
        });
        var createdFullModelPartnerDouble = step("Создание второго Партнера", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setDigitalId(createdFullModelPartner.getDigitalId());
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class);
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartnerDouble);
            partnerChange.setComment("Обновление комментария по партнеру");
            partnerChange.setKpp(createdFullModelPartner.getKpp());
            partnerChange.setInn(createdFullModelPartner.getInn());
            partnerChange.setOrgName(createdFullModelPartner.getOrgName());
            partnerChange.setSecondName(createdFullModelPartner.getSecondName());
            partnerChange.setFirstName(createdFullModelPartner.getFirstName());
            partnerChange.setMiddleName(createdFullModelPartner.getMiddleName());
            return partnerChange;
        });
        var error = step("Выполняем запрос на обновление Партнер.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.BAD_REQUEST,
                partnerChangeFullModel,
                Error.class));
        step("Проверка возвращаемого исключения", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(PARTNER_DUPLICATE_EXCEPTION.getValue());
        });
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление основных реквизитов")
    @MethodSource("partnerArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changePartnerAttributes(String updatedComment, String updatedKpp) {
        var createdFullModelPartner = step("Создание Партнера", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setComment(updatedComment);
            partnerChange.setKpp(updatedKpp);
            return partnerChange;
        });
        var expectedPartnerFullModelResponse = step("Подготавливаем ожидаемый ответ после обновления партнера", () -> {
            var partnerFullModel = clonePartnerFullModelResponse(createdFullModelPartner);
            partnerFullModel.setComment(updatedComment);
            partnerFullModel.setKpp(updatedKpp);
            return partnerFullModel;
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнер.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка основных реквизитов после обновления Партнера", () -> {
            assertThat(actualPartnerFullModelResponse)
                .isNotNull();
            assertThat(actualPartnerFullModelResponse)
                .usingRecursiveComparison()
                .ignoringFields(
                    "changeDate",
                    "version",
                    "phones",
                    "emails",
                    "accounts",
                    "documents",
                    "address",
                    "contacts"
                )
                .isEqualTo(expectedPartnerFullModelResponse);
        });
    }

    static Stream<? extends Arguments> partnerArgumentsForUpdatingPartnerFullModel() {
        return Stream.of(Arguments.of("Обновление комментария по партнеру", "111111111"));
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление телефонов")
    @MethodSource("phoneArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changePhones(Set<String> initialPhones, Set<Pair<String, String>> updatedPhones) {
        var createdFullModelPartner = step("Создание Партнера с телефонами", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setPhones(initialPhones);
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var phoneChangeFullModelList = step("Подготавливаем список телефонов для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedPhones)) {
                return null;
            }
            return updatedPhones.stream()
                .map(updatedPhone -> {
                    if (StringUtils.isEmpty(updatedPhone.getLeft())) {
                        return new PhoneChangeFullModel()
                            .phone(updatedPhone.getRight());
                    }
                    var existedPhone = createdFullModelPartner.getPhones().stream()
                        .filter(phone -> phone.getPhone().equals(updatedPhone.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new PhoneChangeFullModel()
                        .id(existedPhone.getId())
                        .version(existedPhone.getVersion())
                        .phone(updatedPhone.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setPhones(phoneChangeFullModelList);
            return partnerChange;
        });
        var expectedPhones = step("Подготавливаем ожидаемый список телефонов после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialPhones) && CollectionUtils.isEmpty(updatedPhones)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialPhones)) {
                return updatedPhones.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedPhones)) {
                return new ArrayList<>(initialPhones);
            }
            return Stream.concat(
                initialPhones.stream().map(initialPhone ->
                    updatedPhones.stream()
                        .filter(it -> initialPhone.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialPhone)
                ),
                updatedPhones.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка телефонов после обновления Партнера", () -> {
            var actualPhones = actualPartnerFullModelResponse.getPhones().stream()
                .map(Phone::getPhone)
                .collect(Collectors.toList());
            assertThat(actualPhones)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedPhones);
        });
    }

    static Stream<? extends Arguments> phoneArgumentsForUpdatingPartnerFullModel() {
        var initialPhones = Set.of("0079241111111", "0079242222222");
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, "0079241111122"),
                    Pair.of(null, "0079242222233")
                )
            ),
            Arguments.of(
                initialPhones,
                Collections.emptySet()
            ),
            Arguments.of(
                initialPhones,
                Set.of(
                    Pair.of("0079241111111", "0079241111122"),
                    Pair.of("0079242222222", "0079242222233")
                )
            ),
            Arguments.of(
                initialPhones,
                Set.of(
                    Pair.of("0079241111111", "0079241111122"),
                    Pair.of(null, "0079243333333")
                )
            )
        );
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление электронных адресов")
    @MethodSource("emailArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeEmails(Set<String> initialEmails, Set<Pair<String, String>> updatedEmails) {
        var createdFullModelPartner = step("Создание Партнера с электронными адресами", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setEmails(initialEmails);
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var emailChangeFullModelList = step("Подготавливаем список электроных адресов для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedEmails)) {
                return null;
            }
            return updatedEmails.stream()
                .map(updatedEmail -> {
                    if (StringUtils.isEmpty(updatedEmail.getLeft())) {
                        return new EmailChangeFullModel()
                            .email(updatedEmail.getRight());
                    }
                    var existedEmail = createdFullModelPartner.getEmails().stream()
                        .filter(email -> email.getEmail().equals(updatedEmail.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new EmailChangeFullModel()
                        .id(existedEmail.getId())
                        .version(existedEmail.getVersion())
                        .email(updatedEmail.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setEmails(emailChangeFullModelList);
            return partnerChange;
        });
        var expectedEmail = step("Подготавливаем ожидаемый список электронных адресов после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialEmails) && CollectionUtils.isEmpty(updatedEmails)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialEmails)) {
                return updatedEmails.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedEmails)) {
                return new ArrayList<>(initialEmails);
            }
            return Stream.concat(
                initialEmails.stream().map(initialEmail ->
                    updatedEmails.stream()
                        .filter(it -> initialEmail.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialEmail)
                ),
                updatedEmails.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка электронных адресов после обновления Партнера", () -> {
            var actualEmails = actualPartnerFullModelResponse.getEmails().stream()
                .map(Email::getEmail)
                .collect(Collectors.toList());
            assertThat(actualEmails)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedEmail);
        });
    }

    static Stream<? extends Arguments> emailArgumentsForUpdatingPartnerFullModel() {
        var initialEmails = Set.of("11-11-11@mail.ru", "22-22-22@mail.ru");
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, "11-11-11@mail.ru"),
                    Pair.of(null, "22-22-22@mail.ru")
                )
            ),
            Arguments.of(
                initialEmails,
                Collections.emptySet()
            ),
            Arguments.of(
                initialEmails,
                Set.of(
                    Pair.of("11-11-11@mail.ru", "11-11-22@mail.ru"),
                    Pair.of("22-22-22@mail.ru", "22-22-33@mail.ru")
                )
            ),
            Arguments.of(
                initialEmails,
                Set.of(
                    Pair.of("11-11-11@mail.ru", "11-11-22@mail.ru"),
                    Pair.of(null, "33-33-33@mail.ru")
                )
            )
        );
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление адресов")
    @MethodSource("addressArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeAddresses(Set<String> initialAddresses, Set<Pair<String, String>> updatedAddresses) {
        var createdFullModelPartner = step("Создание Партнера с адресами", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setAddress(
                initialAddresses.stream()
                    .map(it -> new AddressCreateFullModel()
                        .type(AddressType.LEGAL_ADDRESS)
                        .city(it))
                    .collect(Collectors.toSet())
            );
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var addressChangeFullModelList = step("Подготавливаем список адресов для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedAddresses)) {
                return null;
            }
            return updatedAddresses.stream()
                .map(updatedAddress -> {
                    if (StringUtils.isEmpty(updatedAddress.getLeft())) {
                        return new AddressChangeFullModel()
                            .type(AddressType.LEGAL_ADDRESS)
                            .city(updatedAddress.getRight());
                    }
                    var existedAddress = createdFullModelPartner.getAddress().stream()
                        .filter(address -> address.getCity().equals(updatedAddress.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new AddressChangeFullModel()
                        .id(existedAddress.getId())
                        .version(existedAddress.getVersion())
                        .type(existedAddress.getType())
                        .city(updatedAddress.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setAddress(addressChangeFullModelList);
            return partnerChange;
        });
        var expectedAddresses = step("Подготавливаем ожидаемый список адресов после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialAddresses) && CollectionUtils.isEmpty(updatedAddresses)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialAddresses)) {
                return updatedAddresses.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedAddresses)) {
                return new ArrayList<>(initialAddresses);
            }
            return Stream.concat(
                initialAddresses.stream().map(initialAddress ->
                    updatedAddresses.stream()
                        .filter(it -> initialAddress.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialAddress)
                ),
                updatedAddresses.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера c адресами.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка адресов после обновления Партнера", () -> {
            var actualAddresses = actualPartnerFullModelResponse.getAddress().stream()
                .map(Address::getCity)
                .collect(Collectors.toList());
            assertThat(actualAddresses)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedAddresses);
        });
    }

    static Stream<? extends Arguments> addressArgumentsForUpdatingPartnerFullModel() {
        var initialAddress1 = "full address 1";
        var initialAddress2 = "full address 2";
        var updatedAddress1 = "full address 3";
        var updatedAddress2 = "full address 4";
        var initialAddresses = Set.of(initialAddress1, initialAddress2);
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, updatedAddress1),
                    Pair.of(null, updatedAddress2)
                )
            ),
            Arguments.of(
                initialAddresses,
                Collections.emptySet()
            ),
            Arguments.of(
                initialAddresses,
                Set.of(
                    Pair.of(initialAddress1, updatedAddress1),
                    Pair.of(initialAddress2, updatedAddress2)
                )
            ),
            Arguments.of(
                initialAddresses,
                Set.of(
                    Pair.of(initialAddress1, updatedAddress1),
                    Pair.of(null, updatedAddress2)
                )
            )
        );
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление документов")
    @MethodSource("documentArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeDocument(Set<String> initialDocuments, Set<Pair<String, String>> updatedDocuments) {
        var documentTypeId = UUID.fromString("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2");
        var createdFullModelPartner = step("Создание Партнера с документами", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setDocuments(
                initialDocuments.stream()
                    .map(it -> new DocumentCreateFullModel()
                        .documentTypeId(documentTypeId)
                        .number("Номер")
                        .series(it)
                    )
                    .collect(Collectors.toSet())
            );
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var documentChangeFullModelList = step("Подготавливаем список документов для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedDocuments)) {
                return null;
            }
            return updatedDocuments.stream()
                .map(updatedDocument -> {
                    if (StringUtils.isEmpty(updatedDocument.getLeft())) {
                        return new DocumentChangeFullModel()
                            .documentTypeId(documentTypeId)
                            .series(updatedDocument.getRight());
                    }
                    var existedDocument = createdFullModelPartner.getDocuments().stream()
                        .filter(document -> document.getSeries().equals(updatedDocument.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new DocumentChangeFullModel()
                        .id(existedDocument.getId())
                        .version(existedDocument.getVersion())
                        .documentTypeId(documentTypeId)
                        .series(updatedDocument.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление документов", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setDocuments(documentChangeFullModelList);
            return partnerChange;
        });
        var expectedDocuments = step("Подготавливаем ожидаемый список документов после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialDocuments) && CollectionUtils.isEmpty(updatedDocuments)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialDocuments)) {
                return updatedDocuments.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedDocuments)) {
                return new ArrayList<>(initialDocuments);
            }
            return Stream.concat(
                initialDocuments.stream().map(initialDocument ->
                    updatedDocuments.stream()
                        .filter(it -> initialDocument.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialDocument)
                ),
                updatedDocuments.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера c документами.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка документов после обновления Партнера", () -> {
            var actualDocuments = actualPartnerFullModelResponse.getDocuments().stream()
                .map(Document::getSeries)
                .collect(Collectors.toList());
            assertThat(actualDocuments)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedDocuments);
        });
    }

    static Stream<? extends Arguments> documentArgumentsForUpdatingPartnerFullModel() {
        var initialDocument1 = "document 1";
        var initialDocument2 = "document 2";
        var updatedDocument1 = "document 3";
        var updatedDocument2 = "document 4";
        var initialDocuments = Set.of(initialDocument1, initialDocument2);
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, updatedDocument1),
                    Pair.of(null, updatedDocument2)
                )
            ),
            Arguments.of(
                initialDocuments,
                Collections.emptySet()
            ),
            Arguments.of(
                initialDocuments,
                Set.of(
                    Pair.of(initialDocument1, updatedDocument1),
                    Pair.of(initialDocument2, updatedDocument2)
                )
            ),
            Arguments.of(
                initialDocuments,
                Set.of(
                    Pair.of(initialDocument1, updatedDocument1),
                    Pair.of(null, updatedDocument2)
                )
            )
        );
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление контактов")
    @MethodSource("contactArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeContact(Set<String> initialContacts, Set<Pair<String, String>> updatedContacts) {
        var createdFullModelPartner = step("Создание Партнера с контактами", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setContacts(
                initialContacts.stream()
                    .map(it -> new ContactCreateFullModel()
                        .legalForm(LegalForm.PHYSICAL_PERSON)
                        .firstName(it)
                    )
                    .collect(Collectors.toSet())
            );
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var contactChangeFullModelList = step("Подготавливаем список контактов для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedContacts)) {
                return null;
            }
            return updatedContacts.stream()
                .map(updatedContact -> {
                    if (StringUtils.isEmpty(updatedContact.getLeft())) {
                        return new ContactChangeFullModel()
                            .legalForm(LegalForm.PHYSICAL_PERSON)
                            .firstName(updatedContact.getRight());
                    }
                    var existedContact = createdFullModelPartner.getContacts().stream()
                        .filter(contact -> contact.getFirstName().equals(updatedContact.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new ContactChangeFullModel()
                        .id(existedContact.getId())
                        .version(existedContact.getVersion())
                        .legalForm(LegalForm.PHYSICAL_PERSON)
                        .firstName(updatedContact.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление контактов", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setContacts(contactChangeFullModelList);
            return partnerChange;
        });
        var expectedContacts = step("Подготавливаем ожидаемый список контактов после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialContacts) && CollectionUtils.isEmpty(updatedContacts)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialContacts)) {
                return updatedContacts.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedContacts)) {
                return new ArrayList<>(initialContacts);
            }
            return Stream.concat(
                initialContacts.stream().map(initialContact ->
                    updatedContacts.stream()
                        .filter(it -> initialContact.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialContact)
                ),
                updatedContacts.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера c контактами.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка контактов после обновления Партнера", () -> {
            var actualContacts = actualPartnerFullModelResponse.getContacts().stream()
                .map(Contact::getFirstName)
                .collect(Collectors.toList());
            assertThat(actualContacts)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedContacts);
        });
    }

    static Stream<? extends Arguments> contactArgumentsForUpdatingPartnerFullModel() {
        var initialContact1 = "contact 1";
        var initialContact2 = "contact 2";
        var updatedContact1 = "contact 3";
        var updatedContact2 = "contact 4";
        var initialContacts = Set.of(initialContact1, initialContact2);
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, updatedContact1),
                    Pair.of(null, updatedContact2)
                )
            ),
            Arguments.of(
                initialContacts,
                Collections.emptySet()
            ),
            Arguments.of(
                initialContacts,
                Set.of(
                    Pair.of(initialContact1, updatedContact1),
                    Pair.of(initialContact2, updatedContact2)
                )
            ),
            Arguments.of(
                initialContacts,
                Set.of(
                    Pair.of(initialContact1, updatedContact1),
                    Pair.of(null, updatedContact2)
                )
            )
        );
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление телефонов контакта")
    @MethodSource("contactPhoneArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeContactPhones(Set<String> initialContactPhones, Set<Pair<String, String>> updatedContactPhones) {
        var createdFullModelPartner = step("Создание Партнера с телефонами контакта", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setContacts(
                Set.of(
                    new ContactCreateFullModel()
                        .legalForm(LegalForm.PHYSICAL_PERSON)
                        .firstName("Контакт")
                        .phones(initialContactPhones)
                )
            );
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var contactPhoneChangeFullModelList = step("Подготавливаем список телефонов контакта для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedContactPhones)) {
                return null;
            }
            return updatedContactPhones.stream()
                .map(updatedContactPhone -> {
                    if (StringUtils.isEmpty(updatedContactPhone.getLeft())) {
                        return new PhoneChangeFullModel()
                            .phone(updatedContactPhone.getRight());
                    }
                    var existedContactPhone = createdFullModelPartner.getContacts().stream()
                        .flatMap(contact -> contact.getPhones().stream())
                        .filter(phone -> phone.getPhone().equals(updatedContactPhone.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new PhoneChangeFullModel()
                        .id(existedContactPhone.getId())
                        .version(existedContactPhone.getVersion())
                        .phone(updatedContactPhone.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление телефонов контакта", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            Optional.ofNullable(contactPhoneChangeFullModelList)
                .ifPresent(it ->
                    Optional.ofNullable(partnerChange.getContacts())
                        .ifPresent(contacts ->
                            contacts.forEach(contact ->
                                contact.setPhones(new ArrayList<>(contactPhoneChangeFullModelList)))
                        )
                );
            return partnerChange;
        });
        var expectedContactPhones = step("Подготавливаем ожидаемый список телефонов контакта после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialContactPhones) && CollectionUtils.isEmpty(updatedContactPhones)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialContactPhones)) {
                return updatedContactPhones.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedContactPhones)) {
                return new ArrayList<>(initialContactPhones);
            }
            return Stream.concat(
                initialContactPhones.stream().map(initialContactPhone ->
                    updatedContactPhones.stream()
                        .filter(it -> initialContactPhone.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialContactPhone)
                ),
                updatedContactPhones.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера c телефонами контакта.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка телефонов контакта после обновления Партнера", () -> {
            var actualPhones = actualPartnerFullModelResponse.getContacts().stream()
                .flatMap(contact -> contact.getPhones().stream())
                .map(Phone::getPhone)
                .collect(Collectors.toList());
            assertThat(actualPhones)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedContactPhones);
        });
    }

    static Stream<? extends Arguments> contactPhoneArgumentsForUpdatingPartnerFullModel() {
        var initialPhone1 = "1111111111111";
        var initialPhone2 = "2222222222222";
        var updatedPhone1 = "1111111111122";
        var updatedPhone2 = "2222222222233";
        var initialPhones = Set.of(initialPhone1, initialPhone2);
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, updatedPhone1),
                    Pair.of(null, updatedPhone2)
                )
            ),
            Arguments.of(
                initialPhones,
                Collections.emptySet()
            ),
            Arguments.of(
                initialPhones,
                Set.of(
                    Pair.of(initialPhone1, updatedPhone1),
                    Pair.of(initialPhone2, updatedPhone2)
                )
            ),
            Arguments.of(
                initialPhones,
                Set.of(
                    Pair.of(initialPhone1, updatedPhone1),
                    Pair.of(null, updatedPhone2)
                )
            )
        );
    }

    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление электронных адресов контакта")
    @MethodSource("contactEmailArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeContactEmails(Set<String> initialContactEmails, Set<Pair<String, String>> updatedContactEmails) {
        var createdFullModelPartner = step("Создание Партнера с электронными адресами контакта", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setContacts(
                Set.of(
                    new ContactCreateFullModel()
                        .legalForm(LegalForm.PHYSICAL_PERSON)
                        .firstName("Контакт")
                        .emails(initialContactEmails)
                )
            );
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var contactEmailChangeFullModelList = step("Подготавливаем список электронных адресов контакта для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedContactEmails)) {
                return null;
            }
            return updatedContactEmails.stream()
                .map(updatedContactEmail -> {
                    if (StringUtils.isEmpty(updatedContactEmail.getLeft())) {
                        return new EmailChangeFullModel()
                            .email(updatedContactEmail.getRight());
                    }
                    var existedContactEmail = createdFullModelPartner.getContacts().stream()
                        .flatMap(contact -> contact.getEmails().stream())
                        .filter(email -> email.getEmail().equals(updatedContactEmail.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new EmailChangeFullModel()
                        .id(existedContactEmail.getId())
                        .version(existedContactEmail.getVersion())
                        .email(updatedContactEmail.getRight());
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление электронных адресов контакта", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            Optional.ofNullable(contactEmailChangeFullModelList).flatMap(it -> Optional.ofNullable(partnerChange.getContacts())).ifPresent(contacts ->
                contacts.forEach(contact ->
                    contact.setEmails(new ArrayList<>(contactEmailChangeFullModelList))));
            return partnerChange;
        });
        var expectedContactEmails = step("Подготавливаем ожидаемый список электронных адресов контакта после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialContactEmails) && CollectionUtils.isEmpty(updatedContactEmails)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialContactEmails)) {
                return updatedContactEmails.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedContactEmails)) {
                return new ArrayList<>(initialContactEmails);
            }
            return Stream.concat(
                initialContactEmails.stream().map(initialContactEmail ->
                    updatedContactEmails.stream()
                        .filter(it -> initialContactEmail.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialContactEmail)
                ),
                updatedContactEmails.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера c электронными адресами контакта.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка электронные адреса контакта после обновления Партнера", () -> {
            var actualEmails = actualPartnerFullModelResponse.getContacts().stream()
                .flatMap(contact -> contact.getEmails().stream())
                .map(Email::getEmail)
                .collect(Collectors.toList());
            assertThat(actualEmails)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedContactEmails);
        });
    }

    static Stream<? extends Arguments> contactEmailArgumentsForUpdatingPartnerFullModel() {
        var initiatEmail1 = "1111@mail.ru";
        var initialEmail2 = "2222@mail.ru";
        var updatedEmail1 = "1122@mail.ru";
        var updatedEmail2 = "2233@mail.ru";
        var initialEmails = Set.of(initiatEmail1, initialEmail2);
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, updatedEmail1),
                    Pair.of(null, updatedEmail2)
                )
            ),
            Arguments.of(
                initialEmails,
                Collections.emptySet()
            ),
            Arguments.of(
                initialEmails,
                Set.of(
                    Pair.of(initiatEmail1, updatedEmail1),
                    Pair.of(initialEmail2, updatedEmail2)
                )
            ),
            Arguments.of(
                initialEmails,
                Set.of(
                    Pair.of(initiatEmail1, updatedEmail1),
                    Pair.of(null, updatedEmail2)
                )
            )
        );
    }

    @Test
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление счета на существующий")
    void testUpdatePartnerFullModel_changeAccountsDuplicate() {
        Set<Pair<String, String>> updatedAccounts = Set.of(
            Pair.of("40702810000000000001", "40702810000000000001"),
            Pair.of("40702810300000000002", "40702810000000000001"));
        var createdFullModelPartner = step("Создание Партнера с счетами", () -> {
            var initialAccounts = Set.of("40702810000000000001", "40702810300000000002");
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setAccounts(
                initialAccounts.stream()
                    .map(PartnerControllerTest::createAccountCreateFullModel)
                    .collect(Collectors.toSet()));
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class);
        });
        var accountChangeFullModelList = step("Подготавливаем список счетов для обновления", () ->
            updatedAccounts.stream()
                .map(updatedAccount -> {
                    var existedAccount = createdFullModelPartner.getAccounts().stream()
                        .filter(account -> account.getAccount().equals(updatedAccount.getLeft()))
                        .findAny().orElse(null);

                    return new AccountChangeFullModel()
                        .id(existedAccount.getId())
                        .version(existedAccount.getVersion())
                        .account(updatedAccount.getRight())
                        .bank(new BankChangeFullModel()
                            .name("Наименование Банка")
                            .bic("044525000"));
                }).collect(Collectors.toSet()));
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setAccounts(accountChangeFullModelList);
            return partnerChange;
        });
        var error = step("Выполняем запрос на обновление Партнера c счетами.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.BAD_REQUEST,
                partnerChangeFullModel,
                Error.class));
        step("Проверка возвращаемого исключения", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(ACCOUNT_DUPLICATE_EXCEPTION.getValue());
        });
    }


    @ParameterizedTest
    @DisplayName("PATCH /partner/full-model обновление партнера с дочерними сущностями. Обновление счетов")
    @MethodSource("accountArgumentsForUpdatingPartnerFullModel")
    void testUpdatePartnerFullModel_changeAccounts(Set<String> initialAccounts, Set<Pair<String, String>> updatedAccounts) {
        var createdFullModelPartner = step("Создание Партнера с счетами", () -> {
            var partnerCreateFullModel = getValidFullModelLegalEntityPartner();
            partnerCreateFullModel.setAccounts(
                initialAccounts.stream()
                    .map(PartnerControllerTest::createAccountCreateFullModel)
                    .collect(Collectors.toSet())
            );
            return post(
                BASE_ROUTE_PATH + "/full-model",
                HttpStatus.CREATED,
                partnerCreateFullModel,
                PartnerFullModelResponse.class
            );
        });
        var accountChangeFullModelList = step("Подготавливаем список счетов для обновления", () -> {
            if (CollectionUtils.isEmpty(updatedAccounts)) {
                return null;
            }
            return updatedAccounts.stream()
                .map(updatedAccount -> {
                    if (StringUtils.isEmpty(updatedAccount.getLeft())) {
                        return new AccountChangeFullModel()
                            .account(updatedAccount.getRight())
                            .bank(new BankChangeFullModel()
                                .name("Наименование Банка")
                                .bic("044525000")
                            );
                    }
                    var existedAccount = createdFullModelPartner.getAccounts().stream()
                        .filter(account -> account.getAccount().equals(updatedAccount.getLeft()))
                        .findAny()
                        .orElse(null);
                    return new AccountChangeFullModel()
                        .id(existedAccount.getId())
                        .version(existedAccount.getVersion())
                        .account(updatedAccount.getRight())
                        .bank(new BankChangeFullModel()
                            .name("Наименование Банка")
                            .bic("044525000")
                        );
                })
                .collect(Collectors.toSet());
        });
        var partnerChangeFullModel = step("Подготавливаем запрос на обновление партнера", () -> {
            var partnerChange = mapToPartnerUpdateFullModel(createdFullModelPartner);
            partnerChange.setAccounts(accountChangeFullModelList);
            return partnerChange;
        });
        var expectedAccounts = step("Подготавливаем ожидаемый список счетов после обновления партнера", () -> {
            if (CollectionUtils.isEmpty(initialAccounts) && CollectionUtils.isEmpty(updatedAccounts)) {
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(initialAccounts)) {
                return updatedAccounts.stream().map(Pair::getRight).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(updatedAccounts)) {
                return new ArrayList<>(initialAccounts);
            }
            return Stream.concat(
                initialAccounts.stream().map(initialAccount ->
                    updatedAccounts.stream()
                        .filter(it -> initialAccount.equals(it.getLeft()))
                        .findFirst()
                        .map(Pair::getRight)
                        .orElse(initialAccount)
                ),
                updatedAccounts.stream()
                    .filter(it -> StringUtils.isEmpty(it.getLeft()))
                    .map(Pair::getRight)
            ).collect(Collectors.toList());
        });
        var actualPartnerFullModelResponse = step("Выполняем запрос на обновление Партнера c счетами.", () ->
            post(
                FULL_MODEL_PARTNER_PATCH_URL,
                HttpStatus.OK,
                partnerChangeFullModel,
                PartnerFullModelResponse.class
            )
        );
        step("Проверка счетов после обновления Партнера", () -> {
            var actualAccounts = actualPartnerFullModelResponse.getAccounts().stream()
                .map(Account::getAccount)
                .collect(Collectors.toList());
            assertThat(actualAccounts)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedAccounts);
        });
    }

    static Stream<? extends Arguments> accountArgumentsForUpdatingPartnerFullModel() {
        var initialAccount1 = "40702810000000000001";
        var initialAccount2 = "40702810300000000002";
        var updatedAccount1 = "40702810100000000011";
        var updatedAccount2 = "40702810500000000022";
        var initialAccounts = Set.of(initialAccount1, initialAccount2);
        return Stream.of(
            Arguments.of(
                Collections.emptySet(),
                Set.of(
                    Pair.of(null, updatedAccount1),
                    Pair.of(null, updatedAccount2)
                )
            ),
            Arguments.of(
                initialAccounts,
                Collections.emptySet()
            ),
            Arguments.of(
                initialAccounts,
                Set.of(
                    Pair.of(initialAccount1, updatedAccount1),
                    Pair.of(initialAccount2, updatedAccount2)
                )
            ),
            Arguments.of(
                initialAccounts,
                Set.of(
                    Pair.of(initialAccount1, updatedAccount1),
                    Pair.of(null, updatedAccount2)
                )
            )
        );
    }

    static public AccountCreateFullModel createAccountCreateFullModel(String account) {
        return new AccountCreateFullModel()
            .account(account)
            .bank(
                new BankCreate()
                    .name("Банк")
                    .bic("044525000"));
    }

    private PartnerChangeFullModel mapToPartnerUpdateFullModel(PartnerFullModelResponse partner) {
        var phones = partner.getPhones() == null ? null : partner.getPhones().stream()
            .map(it ->
                new PhoneChangeFullModel()
                    .id(it.getId())
                    .version(it.getVersion())
                    .phone(it.getPhone())
            )
            .collect(Collectors.toSet());
        var emails = partner.getEmails() == null ? null : partner.getEmails().stream()
            .map(it ->
                new EmailChangeFullModel()
                    .id(it.getId())
                    .version(it.getVersion())
                    .email(it.getEmail())
            )
            .collect(Collectors.toSet());
        var contacts = partner.getContacts() == null ? null : partner.getContacts().stream()
            .map(contact ->
                new ContactChangeFullModel()
                    .id(contact.getId())
                    .version(contact.getVersion())
                    .legalForm(contact.getLegalForm())
                    .firstName(contact.getFirstName())
            )
            .collect(Collectors.toSet());
        return new PartnerChangeFullModel()
            .id(partner.getId())
            .version(partner.getVersion())
            .digitalId(partner.getDigitalId())
            .legalForm(partner.getLegalForm())
            .inn(partner.getInn())
            .orgName(partner.getOrgName())
            .phones(phones)
            .emails(emails)
            .accounts(null)
            .documents(null)
            .address(null)
            .contacts(contacts);
    }

    private PartnerFullModelResponse clonePartnerFullModelResponse(PartnerFullModelResponse partner) {
        return new PartnerFullModelResponse()
            .id(partner.getId())
            .version(partner.getVersion())
            .digitalId(partner.getDigitalId())
            .legalForm(partner.getLegalForm())
            .orgName(partner.getOrgName())
            .firstName(partner.getFirstName())
            .secondName(partner.getSecondName())
            .middleName(partner.getMiddleName())
            .inn(partner.getInn())
            .kpp(partner.getKpp())
            .ogrn(partner.getOgrn())
            .okpo(partner.getOkpo())
            .comment(partner.getComment())
            .phones(partner.getPhones())
            .emails(partner.getEmails())
            .accounts(partner.getAccounts())
            .documents(partner.getDocuments())
            .address(partner.getAddress())
            .contacts(partner.getContacts());
    }

    public static PartnerCreate getValidPartner(String digitalId, LegalForm form) {
        var partner = new PartnerCreate()
            .digitalId(digitalId)
            .legalForm(form)
            .inn(getValidInnNumber(form))
            .kpp("123456789")
            .ogrn(getValidOgrnNumber(form))
            .okpo(getValidOkpoNumber(form))
            .phones(new HashSet<>(List.of("0079241111111")))
            .emails(new HashSet<>(List.of("a.a.a@sberbank.ru")))
            .comment("555555");
        if (form == LegalForm.LEGAL_ENTITY || form == LegalForm.ENTREPRENEUR) {
            return partner
                .orgName(randomAlphabetic(10));
        } else {
            return partner
                .firstName(randomAlphabetic(10))
                .secondName(randomAlphabetic(10))
                .middleName(randomAlphabetic(10));
        }
    }


    public static PartnerCreate getValidLegalEntityPartner() {
        return getValidLegalEntityPartner(randomAlphabetic(10));
    }

    public static PartnerCreate getValidLegalEntityPartner(String digitalId) {
        return getValidPartner(digitalId, LegalForm.LEGAL_ENTITY);
    }

    public static PartnerCreateFullModel getValidFullModelLegalEntityPartner() {
        return getValidFullModelLegalEntityPartner(randomAlphabetic(10));
    }

    public static PartnerCreate getValidPhysicalPersonPartner() {
        return getValidPhysicalPersonPartner(randomAlphabetic(10));
    }

    private static PartnerCreate getValidPhysicalPersonPartner(String digitalId) {
        return getValidPartner(digitalId, LegalForm.PHYSICAL_PERSON);
    }

    private static PartnerCreate getValidEntrepreneurPartner(String digitalId) {
        return getValidPartner(digitalId, LegalForm.ENTREPRENEUR);
    }

    public static PartnerCreateFullModel getValidFullModelLegalEntityPartner(String digitalId) {
        var bic = getBic();
        return new PartnerCreateFullModel()
            .digitalId(digitalId)
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName(randomAlphabetic(10))
            .firstName(randomAlphabetic(10))
            .secondName(randomAlphabetic(10))
            .middleName(randomAlphabetic(10))
            .inn(getValidInnNumber(LegalForm.LEGAL_ENTITY))
            .kpp("123456789")
            .ogrn(getValidOgrnNumber(LegalForm.LEGAL_ENTITY))
            .okpo("12345678")
            .phones(
                Set.of(
                    "0079241111111"
                ))
            .emails(
                Set.of(
                    "a.a.a@sberbank.ru"
                ))
            .comment("555555")
            .accounts(Set.of(new AccountCreateFullModel()
                .account(getValidAccountNumber(bic))
                .comment("Это тестовый комментарий")
                .bank(new BankCreate()
                    .bic(bic)
                    .name(randomAlphabetic(10))
                    .bankAccount(
                        new BankAccountCreate()
                            .bankAccount(getValidCorrAccountNumber(bic)))
                ))
            )
            .documents(Set.of(new DocumentCreateFullModel()
                    .certifierName(RandomStringUtils.randomAlphabetic(100))
                    .certifierType(CertifierType.NOTARY)
                    .dateIssue(LocalDate.now())
                    .divisionCode(RandomStringUtils.randomAlphanumeric(50))
                    .divisionIssue(RandomStringUtils.randomAlphanumeric(250))
                    .number(RandomStringUtils.randomAlphanumeric(50))
                    .series(RandomStringUtils.randomAlphanumeric(50))
                    .positionCertifier(RandomStringUtils.randomAlphanumeric(100))
                    .documentTypeId(UUID.fromString("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2"))
                )
            )
            .address(Set.of(new AddressCreateFullModel()
                    .building("1")
                    .buildingBlock("2")
                    .city("3")
                    .flat("4")
                    .location("5")
                    .region("6")
                    .regionCode("7")
                    .street("8")
                    .type(AddressType.LEGAL_ADDRESS)
                    .zipCode("9")
                )
            )
            .contacts(Set.of(new ContactCreateFullModel()
                    .legalForm(LegalForm.LEGAL_ENTITY)
                    .orgName("Наименование компании")
                    .firstName("Имя клиента")
                    .secondName("Фамилия клиента")
                    .middleName("Отчество клиента")
                    .position("Должность")
                    .phones(
                        Set.of(
                            "0079241111111"
                        ))
                    .emails(
                        Set.of(
                            "a.a.a@sberbank.ru"
                        ))
                )
            );
    }

    public static Partner createValidPartner() {
        return post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(),
            Partner.class
        );
    }

    protected static Partner createValidPartner(PartnerCreate partner) {
        return post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
    }

    protected static Partner createValidPartner(String digitalId) {
        return step("Создание валидного контрагента", () -> post(
            BASE_ROUTE_PATH,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class));
    }

    private static Error createNotValidPartner() {
        var partner = getValidLegalEntityPartner();
        partner.setInn("222222");
        partner.setKpp("JDujsfdwjinfiwef3224234234");
        partner.setOkpo("1234123123123213123123123123");
        partner.setEmails(Set.of
            ("@@@@@@@@@@@@"));
        return post(BASE_ROUTE_PATH, HttpStatus.BAD_REQUEST, partner, Error.class);
    }

    public static Partner updatePartner(Partner partner) {
        return new Partner()
            .id(partner.getId())
            .digitalId(partner.getDigitalId())
            .legalForm(partner.getLegalForm())
            .orgName(partner.getOrgName())
            .secondName(partner.getSecondName())
            .middleName(partner.getMiddleName())
            .phones(partner.getPhones())
            .emails(partner.getEmails())
            .firstName(randomAlphabetic(10))
            .version(partner.getVersion())
            .inn(partner.getInn());
    }

    private Renter createValidRenter(String digitalId) {
        Renter renter = getValidRenter(digitalId);
        return given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post("/sbbol-partners/renter/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
    }
}
