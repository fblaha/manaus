package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@Profile("matchbook")
@ComponentScan("cz.fb.manaus.matchbook")
public class MatchbookDatabaseConfiguration {

}
