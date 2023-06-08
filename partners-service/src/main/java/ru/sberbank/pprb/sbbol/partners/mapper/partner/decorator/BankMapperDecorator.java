package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BankType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;

import java.util.Optional;

import static java.util.Objects.isNull;

public abstract class BankMapperDecorator implements BankMapper {

    @Autowired
    @Qualifier("delegate")
    private BankMapper delegate;

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Override
    public BankEntity toBank(BankCreate bank) {
        var bankEntity = delegate.toBank(bank);
        bankEntity.setType(getBankType(bank));
        return bankEntity;
    }

    @Override
    public BankEntity toBank(Bank bank) {
        var bankEntity = delegate.toBank(bank);
        bankEntity.setType(getBankType(bank));
        return bankEntity;
    }

    @Override
    public void updateBank(Bank bank, @MappingTarget BankEntity bankEntity) {
        delegate.updateBank(bank, bankEntity);
        bankEntity.setType(getBankType(bank));
        if (bank.getBankAccount() != null) {
            if (bankEntity.getBankAccount() == null) {
                bankEntity.setBankAccount(new BankAccountEntity());
            }
            bankAccountMapper.updateBankAccount(bank.getBankAccount(), bankEntity.getBankAccount());
        }
    }

    @Override
    public void patchBank(Bank bank, @MappingTarget BankEntity bankEntity) {
        delegate.patchBank(bank, bankEntity);
        if (bank.getBankAccount() != null) {
            if (bankEntity.getBankAccount() == null) {
                bankEntity.setBankAccount(new BankAccountEntity());
            }
            bankAccountMapper.patchBankAccount(bank.getBankAccount(), bankEntity.getBankAccount());
        }
    }

    @Override
    public Bank toBank(BankChangeFullModel bankChangeFullModel) {
        var bank = delegate.toBank(bankChangeFullModel);
        Optional.ofNullable(bank.getBankAccount())
            .ifPresent(bankAccount -> bankAccount.setBankId(bank.getId()));
        return bank;
    }

    @Override
    public BankType getBankType(Boolean mediary, ru.sberbank.pprb.sbbol.partners.model.BankType bankType) {
        if (isNull(bankType) && Boolean.TRUE.equals(mediary)) {
            return BankType.AGENT;
        }
        return normalizationBankType(bankType);
    }

    protected BankType normalizationBankType(ru.sberbank.pprb.sbbol.partners.model.BankType bankType) {
        if (isNull(bankType)) {
            return BankType.DEFAULT;
        }
        return switch (bankType) {
            case DEFAULT -> BankType.DEFAULT;
            case AGENT -> BankType.AGENT;
            case BENEFICIARY -> BankType.BENEFICIARY;
        };
    }

    protected BankType getBankType(BankCreate bank) {
        return getBankType(bank.getMediary(), bank.getType());
    }

    protected BankType getBankType(Bank bank) {
        return getBankType(bank.getMediary(), bank.getType());
    }
}
