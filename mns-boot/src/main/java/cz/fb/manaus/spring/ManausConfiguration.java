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
        @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+Configuration$")
        ,
        basePackages = {"cz.fb.manaus"}
)
public class ManausConfiguration {
}
