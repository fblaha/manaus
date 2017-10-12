package cz.fb.manaus.spring;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.provider.ExchangeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Configuration
@ComponentScan(value = "cz.fb.manaus.core",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = DatabaseComponent.class),
        })
public class CoreLocalConfiguration {

    public static final String LOOK_AHEAD_EL = "#{systemEnvironment['MNS_LOOK_AHEAD'] ?: 7}";
    public static final String PRODUCTION_PROFILE = "production";
    public static final String TEST_PROFILE = "test";

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
