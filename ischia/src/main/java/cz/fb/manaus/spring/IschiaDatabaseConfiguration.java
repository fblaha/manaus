package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("ischia")
@ComponentScan("cz.fb.manaus.ischia")
public class IschiaDatabaseConfiguration {

}
