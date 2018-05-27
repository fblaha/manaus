package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+LocalConfiguration$"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+DatabaseConfiguration$")
        },
        basePackages = {"cz.fb.manaus"}
)
public class ManausConfiguration {
}
