package cz.fb.manaus.core.money;

import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class AccountMoneyRepository {
    public static final String MONEY_TOTAL = "account.money.total";
    public static final String MONEY_AVAILABLE = "account.money.available";
    private static final Logger log = Logger.getLogger(AccountMoneyRepository.class.getSimpleName());
    @Autowired
    private PropertiesService propertiesService;

    @Transactional
    public void save(AccountMoney accountMoney) {
        propertiesService.setDouble(MONEY_TOTAL, accountMoney.getTotal(), Duration.ofDays(1));
        propertiesService.setDouble(MONEY_AVAILABLE, accountMoney.getAvailable(), Duration.ofDays(1));
        log.log(Level.INFO, "ACCOUNT_MONEY: money update ''{0}''", accountMoney);
    }

}
