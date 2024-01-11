package ru.sberbank.pprb.sbbol.partners.service.legalform;

import static java.util.Arrays.copyOfRange;

public class PartnerNameResolver {

    private static final String FULL_NAME_PATTERN = "^([А-яA-z]+[-\s]?[(А-яA-z)]+)\s+([А-яA-z]+[-\s]?[А-яA-z]+)\s+([А-яA-z]+[-\s]?[А-яA-z]+)$";

    private static final String SHORT_NAME_PATTERN = "^([А-яA-z]+[-\s]?[(А-яA-z)]+)\s+([А-яA-z]+[-\s]?[А-яA-z]+)$";

    public PartnerName getPartnerName(String name) {
        String[] fio = name.split(" ");

        if (fio.length > 2 && fio.length <= 5) {
            if (name.matches(FULL_NAME_PATTERN)) {
                return new PartnerName(fio[0], fio[1], String.join(" ", copyOfRange(fio, 2, fio.length)));
            }
        }
        if (fio.length == 2) {
            if (name.matches(SHORT_NAME_PATTERN)) {
                return new PartnerName(fio[0], fio[1], null);
            }
        }
        return new PartnerName(null, name, null);
    }

    public record PartnerName(String secondName, String firstName, String middleName) {
    }
}
