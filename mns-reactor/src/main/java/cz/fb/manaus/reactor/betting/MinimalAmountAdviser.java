package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.provider.ExchangeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MinimalAmountAdviser implements AmountAdviser {

    @Autowired
    private ExchangeProvider provider;

    @Override
    public double getAmount() {
        return provider.getMinAmount();
    }
}
