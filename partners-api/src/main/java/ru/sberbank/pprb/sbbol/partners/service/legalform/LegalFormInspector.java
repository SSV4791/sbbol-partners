package ru.sberbank.pprb.sbbol.partners.service.legalform;

import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

public class LegalFormInspector {

    private final LegalFormResolver legalFormResolver;
    private final PartnerNameResolver partnerNameResolver;

    public LegalFormInspector(LegalFormResolver legalFormResolver, PartnerNameResolver partnerNameResolver) {
        this.legalFormResolver = legalFormResolver;
        this.partnerNameResolver = partnerNameResolver;
    }

    public void setLegalFormAndPartnerName(PartnerCreate partner) {
        if (isNotEmpty(partner.getLegalForm())) {
            return;
        } else {
            if (isEmpty(partner.getOrgName())) {
                return;
            }
            if (isNotEmpty(partner.getFirstName()) ||
                isNotEmpty(partner.getSecondName()) ||
                isNotEmpty(partner.getMiddleName())) {
                return;
            }
        }

        var legalForm = legalFormResolver.getLegalFormFromInn(partner.getInn());
        partner.setLegalForm(legalForm);

        if (legalForm.equals(PHYSICAL_PERSON)) {
            var name = partnerNameResolver.getPartnerName(partner.getOrgName());
            partner
                .firstName(name.firstName())
                .secondName(name.secondName())
                .middleName(name.middleName());
        }
    }

    public void setLegalFormAndPartnerName(PartnerCreateFullModel partner) {
        if (isNotEmpty(partner.getLegalForm())) {
            return;
        } else {
            if (isEmpty(partner.getOrgName())) {
                return;
            }
            if (isNotEmpty(partner.getFirstName()) ||
                isNotEmpty(partner.getSecondName()) ||
                isNotEmpty(partner.getMiddleName())) {
                return;
            }
        }

        LegalForm legalForm;

        if (isEmpty(partner.getAccounts())) {
            legalForm = legalFormResolver.getLegalFormFromInn(partner.getInn());
        } else {
            var accounts = partner.getAccounts().stream()
                .map(AccountCreateFullModel::getAccount)
                .collect(Collectors.toSet());
            legalForm = legalFormResolver.getLegalFormFromInnAndAccount(partner.getInn(), accounts);
        }

        partner.setLegalForm(legalForm);

        if (legalForm.equals(PHYSICAL_PERSON)) {
            var name = partnerNameResolver.getPartnerName(partner.getOrgName());
            partner
                .firstName(name.firstName())
                .secondName(name.secondName())
                .middleName(name.middleName());
        }
    }
}
