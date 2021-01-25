package ru.sberbank.pprb.sbbol.partners;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.renter.model.CheckResult;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.renter.model.Renter.TypeEnum.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.renter.model.Renter.TypeEnum.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.renter.model.Renter.TypeEnum.PHYSICAL_PERSON;

@Service
public class ValidationServiceImpl implements ValidationService {
    @Override
    public List<CheckResult> check(Renter renter) {
        List<CheckResult> results = Arrays.stream(Check.values())
                .map(check -> check.validate(renter))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(CheckFailure::getField,
                        Collectors.mapping(CheckFailure::getMsg, Collectors.toList())))
                .entrySet().stream()
                .map(entry -> new CheckResult().field(entry.getKey()).message(entry.getValue()))
                .collect(Collectors.toList());
        return results;
    }


    enum Check {
        INN_FORMAT("Проверьте ИНН арендатора. Он содержит ${INVALIDCHARS} символ(-ы)", "inn") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!isEmpty(renter.getInn())) {
                    String invalidChars = CheckUtils.filterValidCharacters(renter.getInn(), CheckUtils.DIGIT_CHAR, ",");
                    if (StringUtils.isNotBlank(invalidChars)) {
                        String msg = this.message.replace("${INVALIDCHARS}", invalidChars);
                        return new CheckFailure(msg, this.field);
                    }
                }
                return null;
            }
        },
        INN_EMPTY("Укажите ИНН арендатора", "inn") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        isEmpty(renter.getInn())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        INN_SIZE_LE("Укажите ИНН арендатора. Оно состоит из 10 или 12 цифр", "inn") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        !isEmpty(renter.getInn()) &&
                        (renter.getInn().length() != 10 || renter.getInn().length() != 12)) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        INN_SIZE_PP("Укажите ИНН арендатора. Оно состоит из  12 цифр. Если не знаете ИНН, оставьте поле пустым", "inn") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (PHYSICAL_PERSON == renter.getType() && !isEmpty(renter.getInn()) &&
                        renter.getInn().length() != 12) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        ACC_FORMAT("Проверьте «счёт арендатора». Он содержит ${INVALIDCHARS} символ(-ы)", "account") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!isEmpty(renter.getAccount())) {
                    String invalidChars = CheckUtils.filterValidCharacters(renter.getAccount(), CheckUtils.DIGIT_CHAR, ",");
                    if (StringUtils.isNotBlank(invalidChars)) {
                        String msg = this.message.replace("${INVALIDCHARS}", invalidChars);
                        return new CheckFailure(msg, this.field);
                    }
                }
                return null;
            }
        },
        ACC_SIZE("Проверьте Счёт арендатора. Он состоит из 20 цифр", "account") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!isEmpty(renter.getAccount()) && renter.getAccount().length() != 20) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        ACC_KEY("Вы указали неверный ключ Счёта арендатора", "account") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!CheckUtils.checkKeyAccount(renter.getAccount(), renter.getBankBic())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        BIC_SIZE("Укажите БИК банка арендатора. Он состоит из 9 цифр", "bankBic") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!isEmpty(renter.getBankBic()) && renter.getBankBic().length() != 9) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        BIC_FORMAT("Проверьте БИК банка арендатора. Он содержит ${INVALIDCHARS} символ(-ы)", "bankBic") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!isEmpty(renter.getInn())) {
                    String invalidChars = CheckUtils.filterValidCharacters(renter.getBankBic(), CheckUtils.DIGIT_CHAR, ",");
                    if (StringUtils.isNotBlank(invalidChars)) {
                        String msg = this.message.replace("${INVALIDCHARS}", invalidChars);
                        return new CheckFailure(msg, this.field);
                    }
                }
                return null;
            }
        },
        BIC_ZERO("Проверьте, что БИК банка арендатора начинается с нуля", "bankBic") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!isEmpty(renter.getBankBic()) && renter.getBankBic().indexOf('0') != 0) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        EMAIL_FORMAT("Адрес введён с ошибкой. Введите адрес электронной почты в правильном формате: mail@vashakompania.ru", "emails") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (!CheckUtils.checkEmail(renter.getEmails())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        LEGAL_NAME("Укажите Наименование арендатора", "legalName") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        isEmpty(renter.getLegalName())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        LAST_NAME("Укажите Фамилию арендатора", "lastName") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (PHYSICAL_PERSON == renter.getType() && isEmpty(renter.getLastName())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        FIRST_NAME("Укажите Имя арендатора", "firstName") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (PHYSICAL_PERSON == renter.getType() && isEmpty(renter.getFirstName())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        KPP_EMPTY("Укажите КПП арендатора", "kpp") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        !isEmpty(renter.getInn()) &&
                        renter.getInn().length() == 10 && isEmpty(renter.getKpp())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        KPP_SIZE("Укажите КПП арендатора. Он состоит из 9 символов", "kpp") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        !isEmpty(renter.getInn()) &&
                        renter.getInn().length() == 10 && "0".equals(renter.getKpp())) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        KPP_SIZE_PP("Укажите КПП арендатора. Он состоит из 9 символов", "kpp") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (PHYSICAL_PERSON == renter.getType() && !isEmpty(renter.getInn()) &&
                        renter.getInn().length() != 10 && !isEmpty(renter.getKpp()) &&
                        renter.getKpp().length() != 9) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        OKPO_SIZE_8("Проверьте ОКПО арендатора. Он состоит из 8 цифр", "okpo") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (PHYSICAL_PERSON == renter.getType() && !isEmpty(renter.getInn()) &&
                        renter.getInn().length() != 10 && !isEmpty(renter.getOkpo()) &&
                        renter.getOkpo().length() != 8) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        OKPO_SIZE_10("Проверьте ОКПО арендатора. Он состоит из 10 цифр", "okpo") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if (PHYSICAL_PERSON == renter.getType() && !isEmpty(renter.getInn()) &&
                        renter.getInn().length() != 12 && !isEmpty(renter.getOkpo()) &&
                        renter.getOkpo().length() != 10) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        OGRN_SIZE_13("Проверьте ОГРН арендатора. Он состоит из 13 цифр", "ogrn") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        !isEmpty(renter.getInn()) && renter.getInn().length() == 10
                        && !isEmpty(renter.getOgrn()) && renter.getOgrn().length() != 13) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },
        OGRN_SIZE_15("Проверьте ОГРНИП арендатора. Он состоит из 15 цифр", "ogrn") {
            @Override
            CheckFailure validate(@Nonnull Renter renter) {
                if ((ENTREPRENEUR == renter.getType() || LEGAL_ENTITY == renter.getType()) &&
                        !isEmpty(renter.getInn()) && renter.getInn().length() == 12
                        && !isEmpty(renter.getOgrn()) && renter.getOgrn().length() != 15) {
                    return new CheckFailure(this.message, this.field);
                }
                return null;
            }
        },


        ;


        /**
         * Метод валидации арендатора
         *
         * @param renter данные арендатора
         * @return если не успешно - CheckFailure, иначе null.
         */
        abstract CheckFailure validate(@Nonnull Renter renter);

        String message;
        String field;

        Check(String message, String field) {
            this.message = message;
            this.field = field;
        }
    }

}
