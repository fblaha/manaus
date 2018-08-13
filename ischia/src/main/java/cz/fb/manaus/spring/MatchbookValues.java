package cz.fb.manaus.spring;

import cz.fb.manaus.reactor.betting.BetContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.function.Function;

@Profile("matchbook")
@Configuration
public class MatchbookValues {

    private static Map<String, Double> RATES = Map.of("moneyline", 0.1);

    @Bean
    public double priceBulldoze() {
        return 100;
    }

    @Bean
    public Function<BetContext, Double> downgradeStrategy() {
        return this::strategy;
    }

    private double strategy(BetContext context) {
        var type = context.getMarketPrices().getMarket().getType();
        return RATES.getOrDefault(type, 0.08);
    }

}
