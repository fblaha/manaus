package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Configuration
@ComponentScan(value = "cz.fb.manaus.betfair",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = DatabaseComponent.class),
        })
@Profile("betfair")
public class BetfairLocalConfiguration {

}
