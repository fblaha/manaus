package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Configuration
@Profile("ischia")
@ComponentScan(value = "cz.fb.manaus.ischia",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = DatabaseComponent.class),
        })
@Import({BetfairValues.class, MatchbookValues.class})
public class IschiaLocalConfiguration {

}
