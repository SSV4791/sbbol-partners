package ru.sberbank.pprb.sbbol.partners.service.legalform;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

public class LegalFormResolver {

    private static final Set<String> masksPhysicalPersonFiveSymbols = Set.of(
        "40803", "40810", "40813", "40817", "40820", "40823", "40824",
        "40826", "40914", "45815", "45817", "45915", "45917", "47411",
        "47468", "47603", "47605", "47608", "47609", "47833", "47835"
    );

    private static final Set<String> masksPhysicalPersonThreeSymbols = Set.of(
        "423", "426", "455", "457"
    );

    private static final Set<String> masksEntrepreneurFiveSymbols = Set.of(
        "40802", "42108", "42109", "42110", "42111", "42112", "42113",
        "42114", "454", "45814", "45914", "47610", "47611", "47832"
    );

    private static final Set<String> masksEntrepreneurThreeSymbols = Set.of(
        "454"
    );

    public LegalForm getLegalFormFromInn(String inn) {
        return (isNotEmpty(inn) && inn.length() == 12)
            ? PHYSICAL_PERSON
            : LEGAL_ENTITY;
    }

    public LegalForm getLegalFormFromInnAndAccount(String inn, Set<String> allAccounts) {
        if (isNotEmpty(inn) && inn.length() == 10) {
            return LEGAL_ENTITY;
        }

        String firstAccount = null;
        if (isNotEmpty(allAccounts)) {
            for (String account : allAccounts) {
                if (isNotEmpty(account) && account.length() == 20) {
                    firstAccount = account;
                    break;
                }
            }
        }

        if (isNotEmpty(inn) && inn.length() == 12) {
            if (isNotEmpty(firstAccount)) {
                if (masksEntrepreneurFiveSymbols.contains(firstAccount.substring(0, 5)) ||
                    masksEntrepreneurThreeSymbols.contains(firstAccount.substring(0, 3))) {
                    return ENTREPRENEUR;
                }
            }
            return PHYSICAL_PERSON;
        }

        if (isNotEmpty(firstAccount)) {
            if (masksEntrepreneurFiveSymbols.contains(firstAccount.substring(0, 5)) ||
                masksEntrepreneurThreeSymbols.contains(firstAccount.substring(0, 3))) {
                return ENTREPRENEUR;
            }
            if (masksPhysicalPersonFiveSymbols.contains(firstAccount.substring(0, 5)) ||
                masksPhysicalPersonThreeSymbols.contains(firstAccount.substring(0, 3))) {
                return PHYSICAL_PERSON;
            }
        }

        return LEGAL_ENTITY;
    }
}

