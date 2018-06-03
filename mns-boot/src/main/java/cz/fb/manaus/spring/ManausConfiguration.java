package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(
        useDefaultFilters = false,
        includeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class),
        basePackages = {"cz.fb.manaus.spring"}
)
public class ManausConfiguration {
}
