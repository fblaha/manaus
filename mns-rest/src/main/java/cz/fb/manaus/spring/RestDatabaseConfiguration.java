package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@Configuration
@ComponentScan(value = "cz.fb.manaus.rest")
@Profile(ManausProfiles.DB_PROFILE)
public class RestDatabaseConfiguration {

}
