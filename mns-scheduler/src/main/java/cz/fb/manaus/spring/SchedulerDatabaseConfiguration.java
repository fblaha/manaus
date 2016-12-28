package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@ComponentScan("cz.fb.manaus.scheduler")
public class SchedulerDatabaseConfiguration {

}
