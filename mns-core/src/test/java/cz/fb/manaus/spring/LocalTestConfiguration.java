package cz.fb.manaus.spring;

import cz.fb.manaus.core.service.PropertiesService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+LocalConfiguration$"),
        basePackages = {"cz.fb.manaus"}
)
public class LocalTestConfiguration {

    @Bean
    @Primary
    @Profile("test")
    public PropertiesService propertiesService() {
        return Mockito.mock(PropertiesService.class);
    }

}
