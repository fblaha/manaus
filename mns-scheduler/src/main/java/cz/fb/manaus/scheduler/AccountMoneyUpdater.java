package cz.fb.manaus.scheduler;

import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.money.AccountMoneyRegistry;
import cz.fb.manaus.core.provider.ProviderConfigurationValidator;
import cz.fb.manaus.reactor.betting.BetService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountMoneyUpdater {

    @Autowired
    private AccountMoneyRegistry accountMoneyRegistry;
    @Autowired
    private BetService betService;
    @Autowired
    private Optional<ProviderConfigurationValidator> validator;


    @Scheduled(fixedDelay = DateUtils.MILLIS_PER_MINUTE * 2)
    public void updateMoney() {
        validator.ifPresent(validator -> {
            if (validator.isConfigured()) {
                AccountMoney accountMoney = betService.getAccountMoney();
                accountMoneyRegistry.register(accountMoney);
            }
        });
    }
}
