package ru.sberbank.pprb.sbbol.partners.service.legalform;

import static java.util.Arrays.copyOfRange;

public class PartnerNameResolver {

    private static final String fullNamePattern = "^([А-яA-z]+[-\s]?[(А-яA-z)]+)\s+([А-яA-z]+[-\s]?[А-яA-z]+)\s+([А-яA-z]+[-\s]?[А-яA-z]+)$";

    private static final String shortNamePattern = "^([А-яA-z]+[-\s]?[(А-яA-z)]+)\s+([А-яA-z]+[-\s]?[А-яA-z]+)$";

    public PartnerName getPartnerName(String name) {
        String[] fio = name.split(" ");

        if (fio.length > 2 && fio.length <= 5) {
            if (name.matches(fullNamePattern)) {
                return new PartnerName(fio[0], fio[1], String.join(" ", copyOfRange(fio, 2, fio.length)));
            }
        }
        if (fio.length == 2) {
            if (name.matches(shortNamePattern)) {
                return new PartnerName(fio[0], fio[1], null);
            }
        }
        return new PartnerName(null, name, null);
    }

    public static record PartnerName(String secondName, String firstName, String middleName) {
    }
}
