package cz.fb.manaus.core.money;

import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.logging.Logger;

@Repository
public class AccountMoneyRepository {
    public static final String MONEY_TOTAL = "account.money.total";
    public static final String MONEY_AVAILABLE = "account.money.available";
    private static final Logger log = Logger.getLogger(AccountMoneyRepository.class.getSimpleName());
    @Autowired
    private PropertiesService propertiesService;

    @Transactional
    public Optional<AccountMoney> getAccountMoney() {
        OptionalDouble total = propertiesService.getDouble(MONEY_TOTAL);
        OptionalDouble available = propertiesService.getDouble(MONEY_AVAILABLE);
        if (total.isPresent() && available.isPresent()) {
            return Optional.of(new AccountMoney(total.getAsDouble(), available.getAsDouble()));
        } else {
            log.warning("Missing account money");
            return Optional.empty();
        }
    }
}