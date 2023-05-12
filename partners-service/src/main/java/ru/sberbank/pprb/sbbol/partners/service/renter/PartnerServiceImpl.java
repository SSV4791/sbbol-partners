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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @deprecated {@link ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService}
 */
@Deprecated(forRemoval = true)
public class PartnerServiceImpl implements RenterService {

    public static final String ERROR_MESSAGE = "Запись не найдена или записей больше 1";

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
            flatRenter.setDigitalId(renter.getDigitalId());
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
            if (savedAccount.getBank() != null) {
                var bank = savedAccount.getBank();
                flatRenter.setBankUuid(bank.getUuid());
                if (bank.getBankAccount() != null) {
                    flatRenter.setBankAccountUuid(bank.getBankAccount().getUuid());
                }
            }
            AddressEntity savedLegalAddress = null;
            if (renter.getLegalAddress() != null) {
                var address = renterPartnerMapper.toAddress(renter.getLegalAddress(), partner.getUuid(), renter.getDigitalId());
                address.setType(AddressType.LEGAL_ADDRESS);
                savedLegalAddress = addressRepository.save(address);
                flatRenter.setLegalAddressUuid(savedLegalAddress.getUuid());
            }
            AddressEntity savedPhysicalAddress = null;
            if (renter.getPhysicalAddress() != null) {
                var address = renterPartnerMapper.toAddress(renter.getPhysicalAddress(), partner.getUuid(), renter.getDigitalId());
                address.setType(AddressType.PHYSICAL_ADDRESS);
                savedPhysicalAddress = addressRepository.save(address);
                flatRenter.setPhysicalAddressUuid(savedPhysicalAddress.getUuid());
            }
            var document = renterPartnerMapper.toDocument(renter, partner.getUuid());
            DocumentTypeEntity documentTypeEntity = null;
            if (renter.getDulType() != null) {
                var documentType = RenterPartnerMapper.toDocumentType(renter.getDulType());
                var foundDocumentType = dictionaryRepository.getBySystemName(documentType.name());
                if (foundDocumentType.isPresent()) {
                    documentTypeEntity = foundDocumentType.get();
                    document.setTypeUuid(documentTypeEntity.getUuid());
                }
            }
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
            var flatRenter = flatRenterRepository.getByPartnerUuid(uuid)
                .orElseThrow(() -> new RuntimeException(ERROR_MESSAGE));
            var partners = partnerRepository.findAllByUuid(flatRenter.getPartnerUuid());
            if (partners.size() != 1) {
                throw new RuntimeException(ERROR_MESSAGE);
            }
            var targetRenter = partners.get(0);
            renterPartnerMapper.updatePartner(renter, targetRenter);
            updatePhone(flatRenter, renter, targetRenter);
            updateEmail(flatRenter, renter, targetRenter);
            var savedPartner = partnerRepository.save(targetRenter);
            if (!CollectionUtils.isEmpty(savedPartner.getPhones()) && savedPartner.getPhones().get(0) != null) {
                flatRenter.setPhoneUuid(savedPartner.getPhones().get(0).getUuid());
            }
            if (!CollectionUtils.isEmpty(savedPartner.getEmails()) && savedPartner.getEmails().get(0) != null) {
                flatRenter.setPhoneUuid(savedPartner.getEmails().get(0).getUuid());
            }
            AddressEntity savedLegalAddress = null;
            if (renter.getLegalAddress() != null) {
                if (flatRenter.getLegalAddressUuid() != null) {
                    var address = addressRepository.getByDigitalIdAndUuid(renter.getDigitalId(), flatRenter.getLegalAddressUuid());
                    if (address.isPresent()) {
                        savedLegalAddress = address.get();
                        renterPartnerMapper.updateAddress(renter.getLegalAddress(), savedLegalAddress);
                    }
                } else {
                    var address = renterPartnerMapper.toAddress(renter.getLegalAddress(), flatRenter.getPartnerUuid(), renter.getDigitalId());
                    savedLegalAddress = addressRepository.save(address);
                    flatRenter.setLegalAddressUuid(savedLegalAddress.getUuid());
                }
            }
            AddressEntity savedPhysicalAddress = null;
            if (renter.getPhysicalAddress() != null) {
                if (flatRenter.getPhysicalAddressUuid() != null) {
                    var address = addressRepository.getByDigitalIdAndUuid(renter.getDigitalId(), flatRenter.getPhysicalAddressUuid());
                    if (address.isPresent()) {
                        savedPhysicalAddress = address.get();
                        renterPartnerMapper.updateAddress(renter.getPhysicalAddress(), savedPhysicalAddress);
                    }
                } else {
                    var address = renterPartnerMapper.toAddress(renter.getPhysicalAddress(), flatRenter.getPartnerUuid(), renter.getDigitalId());
                    savedPhysicalAddress = addressRepository.save(address);
                    flatRenter.setPhysicalAddressUuid(savedPhysicalAddress.getUuid());
                }
            }
            AccountEntity savedAccount = null;
            if (flatRenter.getAccountUuid() != null) {
                var account = accountRepository.getByDigitalIdAndUuid(targetRenter.getDigitalId(), flatRenter.getAccountUuid())
                    .orElseThrow(() -> new RuntimeException(ERROR_MESSAGE));
                renterPartnerMapper.updateAccount(renter, account);
                var mapBank = new HashMap<UUID, BankEntity>();
                var mapBankAccount = new HashMap<UUID, BankAccountEntity>();
                if (account.getBank() != null) {
                    mapBank.put(account.getBank().getUuid(), account.getBank());
                    var bankAccount = account.getBank().getBankAccount();
                    if (bankAccount != null) {
                        mapBankAccount.put(bankAccount.getUuid(), bankAccount);
                    }
                }
                BankEntity bankEntity;
                if (mapBank.containsKey(flatRenter.getBankUuid())) {
                    bankEntity = mapBank.get(flatRenter.getBankUuid());
                    renterPartnerMapper.updateBank(renter, bankEntity);
                } else {
                    bankEntity = new BankEntity();
                    renterPartnerMapper.updateBank(renter, bankEntity);
                    account.setBank(bankEntity);
                }
                if (renter.getBankAccount() != null) {
                    if (mapBankAccount.containsKey(flatRenter.getBankAccountUuid())) {
                        var bankAccount = mapBankAccount.get(flatRenter.getBankAccountUuid());
                        renterPartnerMapper.updateBankAccount(renter, bankAccount);
                    } else {
                        var bankAccount = new BankAccountEntity();
                        renterPartnerMapper.updateBankAccount(renter, bankAccount);
                        bankEntity.setBankAccount(bankAccount);
                        bankAccount.setBank(bankEntity);
                    }
                }
                savedAccount = accountRepository.save(account);
            }
            DocumentEntity savedDocument = null;
            Optional<DocumentTypeEntity> documentType = Optional.empty();
            if (flatRenter.getDocumentUuid() != null) {
                var document = documentRepository.getByDigitalIdAndUuid(targetRenter.getDigitalId(), flatRenter.getDocumentUuid());
                if (document.isPresent()) {
                    renterPartnerMapper.updateDocument(renter, document.get());
                    if (document.get().getType() != null) {
                        if (renter.getDulType() != null) {
                            if (!document.get().getType().getSystemName().equals(renter.getDulType().name())) {
                                var type = RenterPartnerMapper.toDocumentType(renter.getDulType());
                                documentType = dictionaryRepository.getBySystemName(type.name());
                                documentType.ifPresent(documentTypeEntity -> document.get().setTypeUuid(documentTypeEntity.getUuid()));
                            }
                        } else {
                            documentType = dictionaryRepository.getBySystemName(renter.getDulType().name());
                            documentType.ifPresent(documentTypeEntity -> document.get().setType(documentTypeEntity));
                        }
                        savedDocument = documentRepository.save(document.get());
                    }
                }
            } else {
                var document = renterPartnerMapper.toDocument(renter, flatRenter.getPartnerUuid());
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
                documentType.orElse(null)
            );
        } else {
            Renter result = new Renter();
            result.setType(Renter.TypeEnum.PHYSICAL_PERSON);
            result.setCheckResults(checkResults);
            return result;
        }
    }

    private Renter createRenter
        (
            FlatRenter flatRenter,
            PartnerEntity savedPartner,
            AddressEntity savedLegalAddress,
            AddressEntity savedPhysicalAddress,
            AccountEntity savedAccount,
            DocumentEntity savedDocument,
            DocumentTypeEntity documentType
        ) {
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
            phone.setDigitalId(renter.getDigitalId());
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
            email.setDigitalId(renter.getDigitalId());
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
        if (renterIdentifier.getUuid() == null) {
            return null;
        }
        var uuid = UUID.fromString(renterIdentifier.getUuid());
        var partner = partnerRepository.getByDigitalIdAndUuid(renterIdentifier.getDigitalId(), uuid)
            .orElseThrow(() -> new RuntimeException(ERROR_MESSAGE));
        return createRenter(partner);
    }

    private Renter createRenter(PartnerEntity partner) {
        if (partner == null) {
            return null;
        }
        var flatRenter = flatRenterRepository.getByPartnerUuid(partner.getUuid())
            .orElseThrow(() -> new RuntimeException(ERROR_MESSAGE));
        partner.setPhones(partner.getPhones().stream()
            .filter(value -> Objects.equals(value.getUuid(), flatRenter.getPhoneUuid()))
            .collect(Collectors.toList()));
        partner.setEmails(partner.getEmails().stream()
            .filter(value -> Objects.equals(value.getUuid(), flatRenter.getEmailUuid()))
            .collect(Collectors.toList()));
        Renter renter = renterPartnerMapper.toRenter(partner);
        var account = accountRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getAccountUuid());
        account.ifPresent(accountEntity -> renterPartnerMapper.addRenterAccount(accountEntity, renter));
        if (flatRenter.getPhysicalAddressUuid() != null) {
            var address = addressRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getPhysicalAddressUuid());
            address.ifPresent(addressEntity -> renter.setPhysicalAddress(renterPartnerMapper.toRenterAddress(addressEntity)));
        }
        if (flatRenter.getLegalAddressUuid() != null) {
            var address = addressRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getLegalAddressUuid());
            address.ifPresent(addressEntity -> renter.setLegalAddress(renterPartnerMapper.toRenterAddress(addressEntity)));
        }
        if (flatRenter.getDocumentUuid() != null) {
            var documentEntity = documentRepository.getByDigitalIdAndUuid(partner.getDigitalId(), flatRenter.getDocumentUuid());
            if (documentEntity.isPresent()) {
                renterPartnerMapper.addRenterDocument(documentEntity.get(), renter);
                renterPartnerMapper.addRenterDocumentType(documentEntity.get().getType(), renter);
            }
        }
        return renter;
    }
}
