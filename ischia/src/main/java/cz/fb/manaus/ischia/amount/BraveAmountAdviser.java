package cz.fb.manaus.ischia.amount;

import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.betting.MinimalAmountAdviser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class BraveAmountAdviser implements AmountAdviser {

    @Autowired
    private MinimalAmountAdviser minimalAmountAdviser;

    @Override
    public double getAmount() {
        return minimalAmountAdviser.getAmount() + 1d;
    }
}
