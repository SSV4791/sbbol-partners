package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankChangeFullModel;

import java.util.Optional;

public abstract class BankMapperDecorator implements BankMapper {

    @Autowired
    @Qualifier("delegate")
    private BankMapper delegate;

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Override
    public void updateBank(Bank bank, @MappingTarget BankEntity bankEntity) {
        delegate.updateBank(bank, bankEntity);
        if (bank.getBankAccount() != null) {
            if (bankEntity.getBankAccount() == null) {
                bankEntity.setBankAccount(new BankAccountEntity());
            }
            bankAccountMapper.updateBankAccount(bank.getBankAccount(), bankEntity.getBankAccount());
            var bankAccountEntity = bankEntity.getBankAccount();
            bankAccountEntity.setBank(bankEntity);
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
}
