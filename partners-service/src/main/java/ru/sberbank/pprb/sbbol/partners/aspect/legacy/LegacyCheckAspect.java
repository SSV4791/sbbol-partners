package ru.sberbank.pprb.sbbol.partners.aspect.legacy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Order(20)
public class LegacyCheckAspect {

    private final HttpServletRequest servletRequest;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public LegacyCheckAspect(
        HttpServletRequest servletRequest,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        this.servletRequest = servletRequest;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Pointcut("execution(* ru.sberbank.pprb.sbbol.partners..*Controller.*(..))")
    void callPartnersController() {
        // pointcut
    }

    @Before(value = "callPartnersController() && args(account)", argNames = "account")
    void check(AccountPriority account) {
        if (account != null) {
            legacyCheck(account.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(account)", argNames = "account")
    void check(AccountCreate account) {
        if (account != null) {
            legacyCheck(account.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(account)", argNames = "account")
    void check(AccountChange account) {
        if (account != null) {
            legacyCheck(account.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(account)", argNames = "account")
    void check(AccountsFilter account) {
        if (account != null) {
            legacyCheck(account.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(account)", argNames = "account")
    void check(AccountsSignInfo account) {
        if (account != null) {
            legacyCheck(account.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(account)", argNames = "account")
    void check(AccountsSignFilter account) {
        if (account != null) {
            legacyCheck(account.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(address)", argNames = "address")
    void check(AddressCreate address) {
        if (address != null) {
            legacyCheck(address.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(address)", argNames = "address")
    void check(Address address) {
        if (address != null) {
            legacyCheck(address.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(address)", argNames = "address")
    void check(AddressesFilter address) {
        if (address != null) {
            legacyCheck(address.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(contact)", argNames = "contact")
    void check(ContactCreate contact) {
        if (contact != null) {
            legacyCheck(contact.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(contact)", argNames = "contact")
    void check(Contact contact) {
        if (contact != null) {
            legacyCheck(contact.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(contact)", argNames = "contact")
    void check(ContactsFilter contact) {
        if (contact != null) {
            legacyCheck(contact.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(document)", argNames = "document")
    void check(DocumentCreate document) {
        if (document != null) {
            legacyCheck(document.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(document)", argNames = "document")
    void check(Document document) {
        if (document != null) {
            legacyCheck(document.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(document)", argNames = "document")
    void check(DocumentsFilter document) {
        if (document != null) {
            legacyCheck(document.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(email)", argNames = "email")
    void check(EmailCreate email) {
        if (email != null) {
            legacyCheck(email.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(email)", argNames = "email")
    void check(Email email) {
        if (email != null) {
            legacyCheck(email.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(email)", argNames = "email")
    void check(EmailsFilter email) {
        if (email != null) {
            legacyCheck(email.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(phone)", argNames = "phone")
    void check(PhoneCreate phone) {
        if (phone != null) {
            legacyCheck(phone.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(phone)", argNames = "phone")
    void check(Phone phone) {
        if (phone != null) {
            legacyCheck(phone.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(phone)", argNames = "phone")
    void check(PhonesFilter phone) {
        if (phone != null) {
            legacyCheck(phone.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(partner)", argNames = "partner")
    void check(PartnerCreate partner) {
        if (partner != null) {
            legacyCheck(partner.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(partner)", argNames = "partner")
    void check(Partner partner) {
        if (partner != null) {
            legacyCheck(partner.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(partner)", argNames = "partner")
    void check(PartnersFilter partner) {
        if (partner != null) {
            legacyCheck(partner.getDigitalId());
        }
    }

    @Before(value = "callPartnersController() && args(digitalId,..)", argNames = "call, digitalId")
    void check(JoinPoint call, String digitalId) {
        var signature = (MethodSignature) call.getSignature();
        var parameterNames = signature.getParameterNames();
        for (String parameterName : parameterNames) {
            if ("digitalId".equals(parameterName) && digitalId != null) {
                legacyCheck(digitalId);
                return;
            }
        }
    }

    private void legacyCheck(String digitalId) {
        if (digitalId == null) {
            return;
        }
        if (servletRequest != null) {
            var appObj = servletRequest.getHeader("app-obj-to-migrated");
            if (Objects.equals(appObj, "UfsCounterpartiesDictionaryMigratedToPartners")) {
                return;
            }
        }
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
    }
}
