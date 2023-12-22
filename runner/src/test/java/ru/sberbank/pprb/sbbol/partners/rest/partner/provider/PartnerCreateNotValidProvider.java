package ru.sberbank.pprb.sbbol.partners.rest.partner.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidEntrepreneurPartner;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidLegalEntityPartner;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidPhysicalPersonPartner;

public class PartnerCreateNotValidProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(
                //1 testCreatePartnerWithoutDigitalId
                getValidLegalEntityPartner()
                    .digitalId(""),
                List.of(new Descriptions()
                    .field("digitalId")
                    .addMessageItem("Поле обязательно для заполнения")
                    .addMessageItem("размер должен находиться в диапазоне от 1 до 40"))
            ),
            Arguments.of(
                //2 testCreatePartnerWithoutLegalFormAndOrgName
                getValidLegalEntityPartner()
                    .legalForm(null)
                    .orgName(""),
                List.of(new Descriptions()
                    .field("orgName")
                    .addMessageItem("Поле обязательно для заполнения"))
            ),
            Arguments.of(
                //3 testCreatePartnerEmptyOrgName
                getValidLegalEntityPartner()
                    .orgName(""),
                List.of(new Descriptions()
                    .field("orgName")
                    .addMessageItem("Поле обязательно для заполнения"))
            ),
            Arguments.of(
                //4 testCreatePartnerInvalidCharsInOrgName
                getValidLegalEntityPartner()
                    .orgName("[Наименование Ёё\\] [§±]"),
                List.of(new Descriptions()
                    .field("orgName")
                    .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): §±"))
            ),
            Arguments.of(
                //5 savePhysicalPartnerInvalidName
                getValidPhysicalPersonPartner()
                    .firstName("[Имя Ёё] \\ §±`~><"),
                List.of(new Descriptions()
                    .field("firstName")
                    .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): §±"))
            ),
            Arguments.of(
                //6 savePhysicalPartnerInvalidComment
                getValidPhysicalPersonPartner()
                    .comment("[Коммент Ёё §±]"),
                List.of(new Descriptions()
                    .field("comment")
                    .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): [§±]"))
            ),
            Arguments.of(
                //7 savePhysicalPartnerInvalidComment
                getValidPhysicalPersonPartner()
                    .comment("0123456789".repeat(26)),
                List.of(new Descriptions()
                    .field("comment")
                    .addMessageItem("Максимальное количество символов – 255"))
            ),
            Arguments.of(
                //8 testSavePartnerWithInvalidKpp /*1
                getValidLegalEntityPartner()
                    .kpp("1234567890"),
                List.of(new Descriptions()
                    .field("kpp")
                    .addMessageItem("Введён неверный КПП")
                    .addMessageItem("Максимальное количество символов – 9"))
            ),
            Arguments.of(
                //9 testSavePartnerWithInvalidKpp /*2
                getValidLegalEntityPartner()
                    .kpp("[АБВ1234]"),
                List.of(new Descriptions()
                    .field("kpp")
                    .addMessageItem("Введён неверный КПП")
                    .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): [АБВ]"))
            ),
            Arguments.of(
                //10 testSavePartnerWithInvalidKpp /*3
                getValidLegalEntityPartner()
                    .kpp("003456789"),
                List.of(new Descriptions()
                    .field("kpp")
                    .addMessageItem("Введён неверный КПП"))
            ),
            Arguments.of(
                //11 testSavePartner_whenInvalidOgrnLength
                getValidLegalEntityPartner()
                    .ogrn("11"),
                List.of(new Descriptions()
                    .field("ogrn")
                    .addMessageItem("Ключ ОГРН неверен")
                    .addMessageItem("ОГРН должен состоять из 13 цифр"))
            ),
            Arguments.of(
                //12 testSavePartner_whenInvalidOgrnKey
                getValidLegalEntityPartner()
                    .ogrn("1234567890123"),
                List.of(new Descriptions()
                    .field("ogrn")
                    .addMessageItem("Ключ ОГРН неверен"))
            ),
            Arguments.of(
                //13 testSavePartner_whenInvalidOkpoLength
                getValidLegalEntityPartner()
                    .okpo("1234567890123"),
                List.of(new Descriptions()
                    .field("okpo")
                    .addMessageItem("ОКПО должен состоять из 8 цифр"))
            ),
            Arguments.of(
                //14 testSavePartner_whenInvalidOkpoLength
                getValidEntrepreneurPartner(randomAlphabetic(10))
                    .okpo("123"),
                List.of(new Descriptions()
                    .field("okpo")
                    .addMessageItem("ОКПО должен состоять из 10 цифр"))
            ),
            Arguments.of(
                //15 testSavePartner_whenInvalidCharsOkpo
                getValidEntrepreneurPartner(randomAlphabetic(10))
                    .okpo("12345АБВ"),
                List.of(new Descriptions()
                    .field("okpo")
                    .addMessageItem("ОКПО должен состоять из 10 цифр")
                    .addMessageItem("Поле содержит недопустимый(-е) символ(-ы): АБВ"))
            ),
            Arguments.of(
                //16 testSavePartner_whenLegalFormIsNull_and_notValidateOkpo Определение правового статуса
                getValidLegalEntityPartner()
                    .legalForm(null)
                    .okpo("123456789"),
                List.of(new Descriptions()
                    .field("okpo")
                    .addMessageItem("ОКПО должен состоять из 8 цифр"))
            ),
            Arguments.of(
                //17 Определение правового статуса. Проверка валидации. Статус не определен - наличие ФИО и orgName
                getValidPhysicalPersonPartner()
                    .legalForm(null)
                    .orgName("text"),
                List.of(new Descriptions()
                    .field("orgName")
                    .addMessageItem("Невозможно одновременно указать наименование организации и Ф. И. О. контрагента"))
            ),
            Arguments.of(
                //18 Определение правового статуса. Проверка валидации. Статус не определен - наличие ФИО и отсутствие orgName
                getValidPhysicalPersonPartner()
                    .legalForm(null)
                    .orgName(null),
                List.of(new Descriptions()
                    .field("orgName")
                    .addMessageItem("Поле обязательно для заполнения"))
            )
        );
    }
}
