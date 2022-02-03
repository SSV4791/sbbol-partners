package ru.sberbank.pprb.sbbol.partners.service.renter;

import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.entity.renter.FlatRenter;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterPartnerMapper;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.FlatRenterRepository;
import ru.sberbank.pprb.sbbol.renter.model.CheckResult;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.renter.model.RenterListResponse;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Deprecated
public class PartnerServiceImpl implements RenterService {

    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final DocumentRepository documentRepository;
    private final DocumentDictionaryRepository dictionaryRepository;
    private final FlatRenterRepository flatRenterRepository;
    private final ValidationService validationService;
    private final RenterPartnerMapper renterPartnerMapper;

    public PartnerServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AddressRepository addressRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository dictionaryRepository,
        FlatRenterRepository flatRenterRepository,
        ValidationService validationService,
        RenterPartnerMapper renterPartnerMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.addressRepository = addressRepository;
        this.documentRepository = documentRepository;
        this.dictionaryRepository = dictionaryRepository;
        this.flatRenterRepository = flatRenterRepository;
        this.validationService = validationService;
        this.renterPartnerMapper = renterPartnerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public RenterListResponse getRenters(@Nonnull RenterFilter renterFilter) {
        var foundPartner = partnerRepository.findByFilter(renterFilter);
        var renters = new ArrayList<Renter>(foundPartner.size());
        for (var entity : foundPartner) {
            renters.add(createRenter(entity));
        }
        return new RenterListResponse().items(renters);
    }

    @Override
    @Transactional
    public Renter createRenter(@Nonnull Renter renter) {
        List<CheckResult> checkResults = validationService.check(renter);
        if (checkResults.isEmpty()) {
            var flatRenter = new FlatRenter();
            var partner = renterPartnerMapper.toPartner(renter);
            var savedPartner = partnerRepository.save(partner);
            flatRenter.setPartnerUuid(savedPartner.getUuid());
            if (!CollectionUtils.isEmpty(savedPartner.getPhones())) {
                flatRenter.setPhoneUuid(savedPartner.getPhones().get(0).getUuid());
            }
            if (!CollectionUtils.isEmpty(savedPartner.getEmails())) {
                flatRenter.setEmailUuid(savedPartner.getEmails().get(0).getUuid());
            }
            var account = renterPartnerMapper.toAccount(renter);
            account.setPartnerUuid(savedPartner.getUuid());
            var savedAccount = accountRepository.save(account);
            flatRenter.setAccountUuid(savedAccount.getUuid());
            if (!CollectionUtils.isEmpty(savedAccount.getBanks())) {
                var bank = savedAccount.getBanks().get(0);
                flatRenter.setBankUuid(bank.getUuid());
                if (!CollectionUtils.isEmpty(bank.getBankAccounts())) {
                    flatRenter.setBankAccountUuid(bank.getBankAccounts().get(0).getUuid());
                }
            }
            AddressEntity savedLegalAddress = null;
            if (renter.getLegalAddress() != null) {
                var address = renterPartnerMapper.toAddress(renter.getLegalAddress());
                address.setType(AddressType.LEGAL_ADDRESS);
                address.setUnifiedUuid(partner.getUuid());
                address.setDigitalId(partner.getDigitalId());
                savedLegalAddress = addressRepository.save(address);
                flatRenter.setLegalAddressUuid(savedLegalAddress.getUuid());
            }
            AddressEntity savedPhysicalAddress = null;
            if (renter.getPhysicalAddress() != null) {
                var address = renterPartnerMapper.toAddress(renter.getPhysicalAddress());
                address.setType(AddressType.PHYSICAL_ADDRESS);
                address.setUnifiedUuid(partner.getUuid());
                address.setDigitalId(partner.getDigitalId());
                savedPhysicalAddress = addressRepository.save(address);
                flatRenter.setPhysicalAddressUuid(savedPhysicalAddress.getUuid());
            }
            var document = renterPartnerMapper.toDocument(renter);
            DocumentTypeEntity documentTypeEntity = null;
            if (renter.getDulType() != null) {
                var documentType = RenterPartnerMapper.toDocumentType(renter.getDulType());
                documentTypeEntity = dictionaryRepository.getBySystemName(documentType.name());
                document.setTypeUuid(documentTypeEntity.getUuid());
            }
            document.setUnifiedUuid(partner.getUuid());
            var savedDocument = documentRepository.save(document);
            flatRenter.setDocumentUuid(savedDocument.getUuid());
            return createRenter(
                flatRenter,
                savedPartner,
                savedLegalAddress,
                savedPhysicalAddress,
                savedAccount,
                savedDocument,
                documentTypeEntity
            );
        } else {
            var result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    @Override
    @Transactional
    public Renter updateRenter(@Nonnull Renter renter) {
        List<CheckResult> checkResults = validationService.check(renter);
        if (checkResults.isEmpty()) {
            var uuid = UUID.fromString(renter.getUuid());
            var flatRenter = flatRenterRepository.getByPartnerUuid(uuid);
            if (flatRenter == null) {
                throw new RuntimeException("Запись не найдена или записей больше 1");
            }
            var partner = partnerRepository.getByDigitalIdAndUuid(renter.getDigitalId(), flatRenter.getPartnerUuid());
            if (partner == null) {
                throw new RuntimeException("Запись не найдена или записей больше 1");
            }
            renterPartnerMapper.updatePartner(renter, partner);
            var partnerPhoneEntity = updatePhone(flatRenter, renter, partner);
            var partnerEmailEntity = updateEmail(flatRenter, renter, partner);
            var savedPartner = partnerRepository.save(partner);
            if (partnerPhoneEntity != null) {
                flatRenter.setPhoneUuid(partnerPhoneEntity.getUuid());
            }
            if (partnerEmailEntity != null) {
                flatRenter.setEmailUuid(partnerEmailEntity.getUuid());
            }
            AddressEntity savedLegalAddress = null;
            if (renter.getLegalAddress() != null) {
                if (flatRenter.getLegalAddressUuid() != null) {
                    savedLegalAddress = addressRepository.getByDigitalIdAndUuid(renter.getDigitalId(), flatRenter.getLegalAddressUuid());
                    renterPartnerMapper.updateAddress(renter.getLegalAddress(), savedLegalAddress);
                } else {
                    var address = renterPartnerMapper.toAddress(renter.getLegalAddress());
                    savedLegalAddress = addressRepository.save(address);
                    flatRenter.setLegalAddressUuid(savedLegalAddress.getUuid());
                }
            }
            AddressEntity savedPhysicalAddress = null;
            if (renter.getPhysicalAddress() != null) {
                if (flatRenter.getPhysicalAddressUuid() != null) {
                    savedPhysicalAddress = addressRepository.getByDigitalIdAndUuid(renter.getDigitalId(), flatRenter.getPhysicalAddressUuid());
                    renterPartnerMapper.updateAddress(renter.getPhysicalAddress(), savedPhysicalAddress);
                } else {
                    var address = renterPartnerMapper.toAddress(renter.getPhysicalAddress());
                    savedLegalAddress = addressRepository.save(address);
                    flatRenter.setPhysicalAddressUuid(savedLegalAddress.getUuid());
                }
            }
            AccountEntity savedAccount = null;
            if (flatRenter.getAccountUuid() != null) {
                var account = accountRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getAccountUuid());
                var mapBank = new HashMap<UUID, BankEntity>();
                var mapBankAccount = new HashMap<UUID, BankAccountEntity>();
                for (BankEntity bank : account.getBanks()) {
                    mapBank.put(bank.getUuid(), bank);
                    for (BankAccountEntity bankAccount : bank.getBankAccounts()) {
                        mapBankAccount.put(bankAccount.getUuid(), bankAccount);
                    }
                }
                if (mapBank.containsKey(flatRenter.getBankUuid())) {
                    var bankEntity = mapBank.get(flatRenter.getBankUuid());
                    renterPartnerMapper.updateBank(renter, bankEntity);
                } else {
                    var bank = new BankEntity();
                    renterPartnerMapper.updateBank(renter, bank);
                    account.getBanks().add(bank);
                }
                if (renter.getBankAccount() != null) {
                    if (mapBankAccount.containsKey(flatRenter.getBankAccountUuid())) {
                        var bankAccount = mapBankAccount.get(flatRenter.getBankAccountUuid());
                        bankAccount.setAccount(renter.getBankAccount());
                    } else {
                        var bankAccount = new BankAccountEntity();
                        bankAccount.setAccount(renter.getBankAccount());
                        var bankEntity = mapBank.get(flatRenter.getBankUuid());
                        bankEntity.getBankAccounts().add(bankAccount);
                    }
                }
                savedAccount = accountRepository.save(account);
            }
            DocumentEntity savedDocument;
            DocumentTypeEntity documentType = null;
            if (flatRenter.getDocumentUuid() != null) {
                var document = documentRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getDocumentUuid());
                renterPartnerMapper.updateDocument(renter, document);
                if (document.getType() != null) {
                    if (renter.getDulType() != null && !document.getType().getSystemName().equals(renter.getDulType().name())) {
                        var type = RenterPartnerMapper.toDocumentType(renter.getDulType());
                        documentType = dictionaryRepository.getBySystemName(type.name());
                        document.setTypeUuid(documentType.getUuid());
                    }
                } else {
                    if (renter.getDulType() != null) {
                        documentType = dictionaryRepository.getBySystemName(renter.getDulType().name());
                        document.setType(documentType);
                    }
                }
                savedDocument = documentRepository.save(document);
            } else {
                var document = renterPartnerMapper.toDocument(renter);
                savedDocument = documentRepository.save(document);
                flatRenter.setDocumentUuid(savedDocument.getUuid());
            }
            return createRenter(
                flatRenter,
                savedPartner,
                savedLegalAddress,
                savedPhysicalAddress,
                savedAccount,
                savedDocument,
                documentType
            );
        } else {
            Renter result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    private Renter createRenter(FlatRenter flatRenter, PartnerEntity savedPartner, AddressEntity savedLegalAddress, AddressEntity savedPhysicalAddress, AccountEntity savedAccount, DocumentEntity savedDocument, DocumentTypeEntity documentType) {
        flatRenterRepository.save(flatRenter);
        Renter renterResponse = renterPartnerMapper.toRenter(savedPartner);
        renterPartnerMapper.addRenterAccount(savedAccount, renterResponse);
        if (savedLegalAddress != null) {
            renterResponse.setLegalAddress(renterPartnerMapper.toRenterAddress(savedLegalAddress));
        }
        if (savedPhysicalAddress != null) {
            renterResponse.setPhysicalAddress(renterPartnerMapper.toRenterAddress(savedPhysicalAddress));
        }
        renterPartnerMapper.addRenterDocument(savedDocument, renterResponse);
        renterPartnerMapper.addRenterDocumentType(documentType, renterResponse);
        return renterResponse;
    }

    private PartnerPhoneEntity updatePhone(FlatRenter flatRenter, @NotNull Renter renter, PartnerEntity partner) {
        if (renter.getPhoneNumbers() == null) {
            return null;
        }
        if (flatRenter.getPhoneUuid() == null) {
            var phone = new PartnerPhoneEntity();
            phone.setPhone(renter.getPhoneNumbers());
            phone.setPartner(partner);
            partner.getPhones().add(phone);
            return phone;
        } else {
            for (var phone : partner.getPhones()) {
                if (phone.getUuid().equals(flatRenter.getPhoneUuid())) {
                    phone.setPhone(renter.getPhoneNumbers());
                    return phone;
                }
            }
        }
        return null;
    }

    private PartnerEmailEntity updateEmail(FlatRenter flatRenter, @NotNull Renter renter, PartnerEntity partner) {
        if (renter.getEmails() == null) {
            return null;
        }
        if (flatRenter.getEmailUuid() == null) {
            var email = new PartnerEmailEntity();
            email.setEmail(renter.getEmails());
            email.setPartner(partner);
            partner.getEmails().add(email);
            return email;
        } else {
            for (var email : partner.getEmails()) {
                if (email.getUuid().equals(flatRenter.getEmailUuid())) {
                    email.setEmail(renter.getEmails());
                    return email;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Renter getRenter(@Nonnull RenterIdentifier renterIdentifier) {
        var uuid = UUID.fromString(renterIdentifier.getUuid());
        var partner = partnerRepository.getByDigitalIdAndUuid(renterIdentifier.getDigitalId(), uuid);
        return createRenter(partner);
    }

    private Renter createRenter(PartnerEntity partner) {
        if (partner == null) {
            return null;
        }
        var flatRenter = flatRenterRepository.getByPartnerUuid(partner.getUuid());
        partner.setPhones(partner.getPhones().stream().filter(value -> value.getUuid() != flatRenter.getPhoneUuid()).collect(Collectors.toList()));
        partner.setEmails(partner.getEmails().stream().filter(value -> value.getUuid() != flatRenter.getEmailUuid()).collect(Collectors.toList()));
        Renter renter = renterPartnerMapper.toRenter(partner);
        var account = accountRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getAccountUuid());
        if (account != null) {
            var banks = account.getBanks();
            account.setBanks(banks.stream().filter(value -> value.getUuid() != flatRenter.getAccountUuid()).collect(Collectors.toList()));
            if (!CollectionUtils.isEmpty(banks)) {
                var bankAccount = banks.get(0);
                bankAccount.setBankAccounts(bankAccount.getBankAccounts().stream().filter(value -> value.getUuid() != flatRenter.getBankAccountUuid()).collect(Collectors.toList()));
            }
            renterPartnerMapper.addRenterAccount(account, renter);
        }
        if (flatRenter.getPhysicalAddressUuid() != null) {
            var address = addressRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getPhysicalAddressUuid());
            renter.setPhysicalAddress(renterPartnerMapper.toRenterAddress(address));
        }
        if (flatRenter.getLegalAddressUuid() != null) {
            var address = addressRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getLegalAddressUuid());
            renter.setLegalAddress(renterPartnerMapper.toRenterAddress(address));
        }
        if (flatRenter.getDocumentUuid() != null) {
            var documentEntity = documentRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getDocumentUuid());
            renterPartnerMapper.addRenterDocument(documentEntity, renter);
            if (documentEntity != null) {
                renterPartnerMapper.addRenterDocumentType(documentEntity.getType(), renter);
            }
        }
        return renter;
    }
}
