package ru.sberbank.pprb.sbbol.migration.correspondents.mapper.decorator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

public abstract class MigrationPartnerMapperDecorator implements MigrationPartnerMapper {

    public static final int ENTREPRENEUR_INN_LENGTH = 12;
    public static final List<Pattern> PHYSICAL_PERSON_ACCOUNT_MASK = List.of(
        Pattern.compile("^423\\d+$"),
        Pattern.compile("^426\\d+$"),
        Pattern.compile("^455\\d+$"),
        Pattern.compile("^457\\d+$"),
        Pattern.compile("^45815\\d+$"),
        Pattern.compile("^45817\\d+$"),
        Pattern.compile("^45915\\d+$"),
        Pattern.compile("^45917\\d+$"),
        Pattern.compile("^40803\\d+$"),
        Pattern.compile("^40810\\d+$"),
        Pattern.compile("^40813\\d+$"),
        Pattern.compile("^40817\\d+$"),
        Pattern.compile("^40820\\d+$"),
        Pattern.compile("^40824\\d+$"),
        Pattern.compile("^40826\\d+$"),
        Pattern.compile("^40914\\d+$"),
        Pattern.compile("^47411\\d+$"),
        Pattern.compile("^47468\\d+$"),
        Pattern.compile("^47603\\d+$"),
        Pattern.compile("^47605\\d+$"),
        Pattern.compile("^47608\\d+$"),
        Pattern.compile("^47609\\d+$"),
        Pattern.compile("^47833\\d+$"),
        Pattern.compile("^47835\\d+$")
    );

    @Autowired
    @Qualifier("delegate")
    private MigrationPartnerMapper delegate;

    @Override
    public PartnerEntity toPartnerEntity(String digitalId, MigrationCorrespondentCandidate source) {
        PartnerEntity partner = delegate.toPartnerEntity(digitalId, source);
        setLegalTypeAndNamePartner(source, partner);
        afterMapping(partner);
        return partner;
    }

    public AccountEntity toAccountEntity(String digitalId, UUID partnerUuid, MigrationCorrespondentCandidate source) {
        var accountEntity = delegate.toAccountEntity(digitalId, partnerUuid, source);
        return fillIdLinks(accountEntity, source.getReplicationGuid());
    }

    @Override
    public void updatePartnerEntity(String digitalId, MigrationCorrespondentCandidate source, PartnerEntity partner) {
        delegate.updatePartnerEntity(digitalId, source, partner);
        afterMapping(partner);
    }

    @Override
    public void updateAccountEntity(String digitalId, UUID partnerUuid, MigrationCorrespondentCandidate source, AccountEntity account) {
        delegate.updateAccountEntity(digitalId, partnerUuid, source, account);
    }

    @Override
    public MigrationCorrespondentCandidate toCounterparty(PartnerEntity partner, AccountEntity account) {
        return delegate.toCounterparty(partner, account);
    }

    private void setLegalTypeAndNamePartner(MigrationCorrespondentCandidate source, PartnerEntity partner) {
        LegalType legalType = toLegalType(source.getInn(), source.getAccount());
        partner.setLegalType(legalType);
        partner.setOrgName(legalType != LegalType.PHYSICAL_PERSON ? source.getName() : null);
        partner.setFirstName(legalType == LegalType.PHYSICAL_PERSON ? source.getName() : null);
    }

    public LegalType toLegalType(String inn, String account) {
        if (checkCounterpartyForPhysicalPerson(account)) {
            return LegalType.PHYSICAL_PERSON;
        }
        if (checkCounterpartyForEntrepreneur(inn)) {
            return LegalType.ENTREPRENEUR;
        }
        return LegalType.LEGAL_ENTITY;
    }


    private boolean checkCounterpartyForPhysicalPerson(String account) {
        return StringUtils.isNotEmpty(account) && checkMask(account);
    }

    private boolean checkMask(String account) {
        if (StringUtils.isBlank(account)) {
            return false;
        }
        for (Pattern pattern : PHYSICAL_PERSON_ACCOUNT_MASK) {
            Matcher matcher = pattern.matcher(account);
            if (!matcher.matches()) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean checkCounterpartyForEntrepreneur(String inn) {
        return StringUtils.isNotEmpty(inn) && inn.length() == ENTREPRENEUR_INN_LENGTH;
    }

    private void afterMapping(PartnerEntity partner) {
        var searchSubString =
            prepareSearchString(partner.getInn(),
                partner.getKpp(),
                partner.getOrgName(),
                partner.getSecondName(),
                partner.getFirstName(),
                partner.getMiddleName()
            );
        partner.setSearch(searchSubString);
        var phones = partner.getPhones();
        if (phones != null) {
            for (var phone : phones) {
                if (phone != null) {
                    phone.setPartner(partner);
                }
            }
        }
        var emails = partner.getEmails();
        if (emails != null) {
            for (var email : emails) {
                if (email != null) {
                    email.setPartner(partner);
                }
            }
        }
    }
}
