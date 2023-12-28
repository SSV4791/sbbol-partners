package ru.sberbank.pprb.sbbol.partners.rest.partner.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidAccountNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidCorrAccountNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidInnNumber;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidFullModelLegalEntityPartner;

public class ValidationPartnerCreateFullModelProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(
                //1 savePartnerFullModel_physicalPersonNotSetBudgetCorrAccount
                getValidFullModelLegalEntityPartner()
                    .legalForm(PHYSICAL_PERSON)
                    .accounts(Set.of(
                        new AccountCreateFullModel()
                            .account(getValidAccountNumber("123525411"))
                            .comment("Это тестовый комментарий")
                            .bank(new BankCreate()
                                .bic("123525411")
                                .name(randomAlphabetic(10))
                                .bankAccount(
                                    new BankAccountCreate()
                                        .bankAccount("40102643145250000411"))))),
                List.of(
                    new Descriptions()
                        .field("accounts[0].account")
                        .addMessageItem("Казначейский счёт должен иметь код валюты 643")
                        .addMessageItem("Казначейский счёт должен начинаться с нуля"),
                    new Descriptions()
                        .field("inn")
                        .addMessageItem("ИНН должен состоять из 12 цифр"),
                    new Descriptions()
                        .field("accounts[0].bank.bankAccount.bankAccount")
                        .addMessageItem("Единый казначейский счёт не должен использоваться для физического лица или ИП")
                )
            ),
            Arguments.of(
                //2 savePartnerFullModel_notValidateCodeCurrencyAndBalance
                getValidFullModelLegalEntityPartner()
                    .accounts(Set.of(
                        new AccountCreateFullModel()
                            .account("00101643145250000411")
                            .comment("Это тестовый комментарий")
                            .bank(new BankCreate()
                                .bic("123525411")
                                .name(randomAlphabetic(10))
                                .bankAccount(
                                    new BankAccountCreate()
                                        .bankAccount(getValidCorrAccountNumber("123525411")))))),
                List.of(
                    new Descriptions()
                        .field("accounts[].account")
                        .addMessageItem("Рублёвый счёт должен иметь код валюты 810"),
                    new Descriptions()
                        .field("accounts[0].bank.bankAccount.bankAccount")
                        .addMessageItem("Единый казначейский счёт должен начинаться с 40102")
                )
            ),
            Arguments.of(
                //3 savePartnerFullModelEmptyOrgName
                getValidFullModelLegalEntityPartner()
                    .orgName(""),
                List.of(
                    new Descriptions()
                        .field("orgName")
                        .addMessageItem("Поле обязательно для заполнения")
                )
            ),
            Arguments.of(
                //4 savePartnerFullModelInvalidOrgName
                getValidFullModelLegalEntityPartner()
                    .orgName("[Наименование Ёё \\] §±`~><"),
                List.of(
                    new Descriptions()
                        .field("orgName")
                        .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): §±")
                )
            ),
            Arguments.of(
                //5 savePartnerFullModelInvalidBankName #1
                getValidFullModelLegalEntityPartner()
                    .accounts(
                        Set.of(getAccountFullModelWithNotValidBankName(""))),
                List.of(
                    new Descriptions()
                        .field("accounts[].bank.name")
                        .addMessageItem("Поле обязательно для заполнения")
                )
            ),
            Arguments.of(
                //6 savePartnerFullModelInvalidBankName #2
                getValidFullModelLegalEntityPartner()
                    .accounts(
                        Set.of(getAccountFullModelWithNotValidBankName("Наименование банка [§±]"))),
                List.of(
                    new Descriptions()
                        .field("accounts[].bank.name")
                        .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): [§±]")
                )
            ),
            Arguments.of(
                //7 savePartnerFullModelInvalidBankName #3
                getValidFullModelLegalEntityPartner()
                    .accounts(
                        Set.of(getAccountFullModelWithNotValidBankName("0123456789".repeat(17)))),
                List.of(
                    new Descriptions()
                        .field("accounts[].bank.name")
                        .addMessageItem("Максимальное количество символов – 160")
                )
            ),
            Arguments.of(
                //8 saveFullModelPartnerInvalidAccountComment #1
                getValidFullModelLegalEntityPartner()
                    .accounts(
                        Set.of(getAccountFullModelWithNotValidComment("[Comment §± Ёё]"))),
                List.of(
                    new Descriptions()
                        .field("accounts[].comment")
                        .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): [§±]")
                )
            ),
            Arguments.of(
                //9 saveFullModelPartnerInvalidAccountComment #2
                getValidFullModelLegalEntityPartner()
                    .accounts(
                        Set.of(getAccountFullModelWithNotValidComment("0123456789".repeat(6)))),
                List.of(
                    new Descriptions()
                        .field("accounts[].comment")
                        .addMessageItem("Максимальное количество символов – 50")
                )
            ),
            Arguments.of(
                //10 Определение правового статуса. Проверка валидации. Статус не определен - отсутствует orgName
                getValidFullModelLegalEntityPartner()
                    .legalForm(null)
                    .firstName(null)
                    .secondName(null)
                    .middleName(null)
                    .accounts(null) // для проверки по ИНН
                    .inn("263516479611")
                    .orgName(null),
                List.of(
                    new Descriptions()
                        .field("orgName")
                        .addMessageItem("Поле обязательно для заполнения")
                )
            ),
            Arguments.of(
                //11 Определение правового статуса. Проверка валидации. Статус не определен - наличие ФИО и orgName
                getValidFullModelLegalEntityPartner()
                    .legalForm(null)
                    .accounts(null) // для проверки по ИНН
                    .inn("263516479611"),
                List.of(
                    new Descriptions()
                        .field("orgName")
                        .addMessageItem("Невозможно одновременно указать наименование организации и Ф. И. О. контрагента")
                )
            ),
            Arguments.of(
                //12 Определение правового статуса. Проверка валидации. Статус не определен - наличие ФИО и отсутствие orgName
                getValidFullModelLegalEntityPartner()
                    .legalForm(null)
                    .firstName("firstName")
                    .secondName("secondName")
                    .middleName("middleName")
                    .accounts(null) // для проверки по ИНН
                    .inn("263516479611")
                    .orgName(null),
                List.of(
                    new Descriptions()
                        .field("orgName")
                        .addMessageItem("Поле обязательно для заполнения")
                )
            ),
            Arguments.of(
                //13 Определение правового статуса. Проверка валидации. Статус определен как ФЛ - тип адреса не соответствует
                getValidFullModelLegalEntityPartner()
                    .legalForm(null)
                    .inn(getValidInnNumber(PHYSICAL_PERSON))
                    .firstName(null)
                    .secondName(null)
                    .middleName(null)
                    .address(null)
                    .orgName("Иванов Иван Иванович")
                    .address(Set.of(new AddressCreateFullModel().type(AddressType.LEGAL_ADDRESS))),
                List.of(
                    new Descriptions()
                        .field("address[0].type")
                        .addMessageItem("Указанный адрес не соответствует типу адреса для физлица")
                )
            ),
            Arguments.of(
                //14 Определение правового статуса. Проверка валидации. Статус определен как ЮЛ - тип адреса не соответствует
                getValidFullModelLegalEntityPartner()
                    .legalForm(null)
                    .inn(getValidInnNumber(LEGAL_ENTITY))
                    .firstName(null)
                    .secondName(null)
                    .middleName(null)
                    .address(null)
                    .orgName("Иванов Иван Иванович")
                    .address(Set.of(new AddressCreateFullModel().type(AddressType.RESIDENTIAL_ADDRESS))),
                List.of(
                    new Descriptions()
                        .field("address[0].type")
                        .addMessageItem("Указанный адрес не соответствует типу адреса для юрлица или ИП")
                )
            )
        );
    }

    private AccountCreateFullModel getAccountFullModelWithNotValidBankName(String bankName) {
        return new AccountCreateFullModel()
            .account(getValidAccountNumber("123525411"))
            .comment("Это тестовый комментарий")
            .bank(new BankCreate()
                .bic("123525411")
                .name(bankName)
                .bankAccount(
                    new BankAccountCreate()
                        .bankAccount(getValidCorrAccountNumber("123525411"))));
    }

    private AccountCreateFullModel getAccountFullModelWithNotValidComment(String comment) {
        return new AccountCreateFullModel()
            .account(getValidAccountNumber("123525411"))
            .comment(comment)
            .bank(new BankCreate()
                .bic("123525411")
                .name(randomAlphabetic(10))
                .bankAccount(
                    new BankAccountCreate()
                        .bankAccount(getValidCorrAccountNumber("123525411"))));
    }
}
