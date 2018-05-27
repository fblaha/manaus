package cz.fb.manaus.spring;

import cz.fb.manaus.ischia.strategy.MinimizeChargeStrategy;
import cz.fb.manaus.reactor.betting.BetContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Function;

@Configuration
@Profile("betfair")
public class BetfairValues {

    @Bean
    public double priceBulldoze() {
        return 50;
    }

    @Bean
    public Function<BetContext, Double> downgradeStrategy() {
        return minimizeChargeStrategy()::getReductionRate;
    }

    @Bean
    public MinimizeChargeStrategy minimizeChargeStrategy() {
        return new MinimizeChargeStrategy(0.01, 0.05, 0.06);
    }

}
