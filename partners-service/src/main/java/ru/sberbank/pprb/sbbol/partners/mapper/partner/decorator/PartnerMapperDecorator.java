package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PartnerMapperDecorator implements PartnerMapper {

    @Autowired
    @Qualifier("delegate")
    private PartnerMapper delegate;

    @Autowired
    private PartnerPhoneMapper phoneMapper;

    @Autowired
    private PartnerEmailMapper emailMapper;

    @Override
    public void patchPartner(PartnerChangeFullModel partner, PartnerEntity partnerEntity) {
        var mergedPhones = mergePhonesForPatchingPartner(partnerEntity.getPhones(), partner.getPhones());
        if (!CollectionUtils.isEmpty(mergedPhones)) {
            partner.setPhones(new HashSet<>(mergedPhones));
        }
        var mergedEmails = mergeEmailsForPatchingPartner(partnerEntity.getEmails(), partner.getEmails());
        if (!CollectionUtils.isEmpty(mergedEmails)) {
            partner.setEmails(new HashSet<>(mergedEmails));
        }
        delegate.patchPartner(partner, partnerEntity);
    }

    private List<PhoneChangeFullModel> mergePhonesForPatchingPartner(
        List<PartnerPhoneEntity> initialPhones,
        Set<PhoneChangeFullModel> updatedPhones
    ) {
        if (CollectionUtils.isEmpty(initialPhones) && CollectionUtils.isEmpty(updatedPhones)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(initialPhones)) {
            return new ArrayList<>(updatedPhones);
        }
        if (CollectionUtils.isEmpty(updatedPhones)) {
            return initialPhones.stream()
                .map(phoneMapper::toPhoneChangeFullModel)
                .collect(Collectors.toList());
        }
        var upgradedInitialPhones = initialPhones.stream()
            .filter(initialPhone ->
                updatedPhones.stream()
                    .noneMatch(updatedPhone -> initialPhone.getUuid().toString().equals(updatedPhone.getId()))
            )
            .map(phoneMapper::toPhoneChangeFullModel)
            .collect(Collectors.toList());
        return Stream.concat(upgradedInitialPhones.stream(), updatedPhones.stream())
            .collect(Collectors.toList());
    }

    private List<EmailChangeFullModel> mergeEmailsForPatchingPartner(
        List<PartnerEmailEntity> initialEmails,
        Set<EmailChangeFullModel> updatedEmails
    ) {
        if (CollectionUtils.isEmpty(initialEmails) && CollectionUtils.isEmpty(updatedEmails)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(initialEmails)) {
            return new ArrayList<>(updatedEmails);
        }
        if (CollectionUtils.isEmpty(updatedEmails)) {
            return initialEmails.stream()
                .map(emailMapper::toEmailChangeFullModel)
                .collect(Collectors.toList());
        }
        var upgradedInitialEmails = initialEmails.stream()
            .filter(initialEmail ->
                updatedEmails.stream()
                    .noneMatch(updatedEmail -> initialEmail.getUuid().toString().equals(updatedEmail.getId()))
            )
            .map(emailMapper::toEmailChangeFullModel)
            .collect(Collectors.toList());
        return Stream.concat(upgradedInitialEmails.stream(), updatedEmails.stream())
            .collect(Collectors.toList());
    }
}
