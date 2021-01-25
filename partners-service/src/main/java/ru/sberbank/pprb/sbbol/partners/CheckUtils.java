package ru.sberbank.pprb.sbbol.partners;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {

    public static final String DIGIT_CHAR = "1234567890";
    public static final String LEGAL_NAME_PATTERN = "^[A-Za-zА-Яа-я0-9Ёё!\"№#$%&'() *+,-./:;<=>?@\\[\\\\\\]^_`{|}~\\r\\n]+$";
    private static final String WHITESPACE = " ";
    private static final String AT_CHAR = "@";
    /**
     * регулярное выражение, проверяющее корректность заполнения поля "Электронная почта"
     */
    public static final Pattern EMAIL_PATTERN = Pattern
            .compile("^([A-Za-z0-9-<'_+>]+(\\.[A-Za-z0-9-<'_+>]+)*)@([A-Za-z0-9-<'_+>]+(\\.[A-Za-z0-9-<'_+>]+)*(\\.[A-Za-z]{2,}))$");
    /**
     * регулярное выражение, определяющее наличие в поле "Электронная почта" IDN (интернационализованные доменные имена, пример: <ящик.рф>)
     */
    private static final Pattern IDN_EMAIL_PATTERN = Pattern.compile("^.*@(.*)([а-яА-Я]{1,}).*$");

    /**
     * регулярное выражение, проверяющее наличие IP адреса в домене в поле "Электронная почта"
     */
    private static final Pattern IP_DOMAIN_EMAIL_PATTERN = Pattern.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})");

    /**
     * регулярное выражение, проверяющее наличие специальных символов в поле "Электронная почта"
     */
    private static final Pattern SPECIAL_CHARS_EMAIL_PATTERN = Pattern.compile("[\\*\\%\\^\\$\\#\\!\\[\\]\\{\\}\\?]");

    /**
     * регулярное выражение, проверяющее наличие Escape-символов и кавычек в поле "Электронная почта"
     */
    private static final Pattern ESCAPE_CHARS_EMAIL_PATTERN = Pattern.compile("[\\\\\\/\\`\"\\']");

    private static final int[] koeff = new int[]{7, 1, 3};

    /**
     * Отфильтровывает из строки недопустимые символы.
     *
     * @param target          фильтруемая строка
     * @param validCharacters строка из допустимых символов
     * @param delimiter       разделитель для строки с недопустимыми символами
     * @return {@code null} - если недопустимых символов нет, в противном случае - строку из недопустимых символов, без повторений, через разделитель.
     */
    public static String filterValidCharacters(String target, String validCharacters, String delimiter) {
        Set<Character> invalid = filterValidCharacters(target, validCharacters);
        return invalid.isEmpty() ? null : StringUtils.join(invalid, delimiter);
    }

    public static Set<Character> filterValidCharacters(String target, String validCharacters) {
        Set<Character> invalid = new TreeSet<Character>();
        char[] chars = target.toCharArray();
        for (char ch : chars) {
            if (validCharacters.indexOf(ch) < 0) {
                invalid.add(ch);
            }
        }
        return invalid;
    }

    /**
     * Проверяет адрес E-MAIL по следующим правилам:
     * <ul>
     * <li>E-MAIL не должен содержать пробелы</li>
     * <li>E-MAIL не должен содержать кавычки или Escape-символы</li>
     * <li>E-MAIL не должен содержать специальные символы кроме: <'._+-></li>
     * <li>E-MAIL не должен содержать IP-адрес в поле домен</li>
     * <li>E-MAIL не должен содержать IDN (интернационализованные доменные имена, пример: <ящик.рф>)</li>
     * <li>Длина E-MAIL не должна превышать 129 символов (64 в имени пользователя+@+64 в имени домена)</li>
     * <li>E-MAIL должен соответствовать регулярному выражению</li>
     * </ul>
     * <p>
     * При нахождени первой ошибки из перечисленных возвращает false. если все проверки пройдены возращает true.
     *
     * @param email адрес E-MAIL
     */
    public static boolean checkEmail(String email) {

        if (StringUtils.isEmpty(email)) {
            return false;
        }

        if (email.contains(WHITESPACE)) {
            return false;
        }

        Matcher emailIdnMatcher = IDN_EMAIL_PATTERN.matcher(email);
        if (emailIdnMatcher.matches()) {
            return false;
        }

        Matcher specialCharsMatcher = SPECIAL_CHARS_EMAIL_PATTERN.matcher(email);
        if (specialCharsMatcher.find()) {
            return false;
        }

        Matcher escapeCharsMatcher = ESCAPE_CHARS_EMAIL_PATTERN.matcher(email);
        if (escapeCharsMatcher.find()) {
            return false;
        }

        if (email.contains(AT_CHAR)) {
            String[] splittedEmail = email.split(AT_CHAR);
            if (splittedEmail.length < 2) {
                return false;
            }
            String userName = splittedEmail[0];
            String domain = splittedEmail[1];
            if (StringUtils.isNotEmpty(domain)) {
                Matcher ipDomainMatcher = IP_DOMAIN_EMAIL_PATTERN.matcher(domain);
                if (ipDomainMatcher.matches()) {
                    return false;
                }
                if (StringUtils.isNotEmpty(userName))
                    if (userName.length() > 64 || domain.length() > 64) {
                        return false;
                    }
            }
        }

        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }

        return true;
    }

    /**
     * Проверка на контрольный ключа р/с
     * @param account счет
     * @param bic БИК
     * @return true- проверка успешно пройдена, false не пройдена
     */
    public static boolean checkKeyAccount(String account, String bic) {
        if (StringUtils.isEmpty(account) || account.length() != 20 ||
                StringUtils.isEmpty(bic) || bic.length() != 9) {
            return true; // Если счет или бик не корректный дальше даже не проверяем.
        }

        int[] a = new int[20];
        int[] b = new int[9];
        for (int i = 0; i < 20; i++) {
            int value = Character.digit(account.charAt(i), 10);
            if (value < 0) {
                return true; // Проверка на некоректные символы отдельная, так что если упали то чекер не срабатывает
            }
            a[i] = value;
        }
        for (int i = 0; i < 9; i++) {
            int value = Character.digit(bic.charAt(i), 10);
            if (value < 0) {
                return true; // опять таки не проверям
            }
            b[i] = value;
        }

        boolean isRKZ = b[6] == 0 && b[7] == 0;
        int[] vector = new int[23];
        if (isRKZ) {
            System.arraycopy(b, 4, vector, 1, 2);
        } else {
            System.arraycopy(b, 6, vector, 0, 3);
        }
        System.arraycopy(a, 0, vector, 3, 20);
        vector[3 + 8] = 0;

        int sum = 0;
        for (int i = 0; i < 23; i++)
            sum += vector[i] * koeff[i % 3];

        return (sum * 3) % 10 == a[8];
    }

}
