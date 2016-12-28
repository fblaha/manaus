package cz.fb.manaus.core.money;

import cz.fb.manaus.core.model.AccountMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class AccountMoneyRegistry {

    private final AtomicReference<AccountMoney> reference = new AtomicReference<>();

    @Autowired
    private Optional<AccountMoneyRepository> moneyRepository;

    public void register(AccountMoney accountMoney) {
        reference.set(accountMoney);
        moneyRepository.ifPresent(repository -> repository.save(accountMoney));
    }

    public Optional<AccountMoney> getMoney() {
        return Optional.ofNullable(reference.get());
    }

}
