package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@Configuration
@Import(LocalTestConfiguration.class)
@ComponentScan(
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+DatabaseConfiguration$"),
        basePackages = "cz.fb.manaus"
)
public class DatabaseTestConfiguration {

}
