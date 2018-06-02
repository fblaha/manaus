package cz.fb.manaus.spring;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.provider.ExchangeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(value = "cz.fb.manaus.core")
public class CoreLocalConfiguration {

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    @Profile("matchbook")
    public ExchangeProvider matchbookExchangeProvider() {
        return new ExchangeProvider("matchbook", 2d, 1.001d, 0.0075d, false);
    }

    @Bean
    @Profile("betfair")
    public ExchangeProvider betfairExchangeProvider() {
        return new ExchangeProvider("betfair", 2d, 1.01d, 0.065d, true);
    }
}
