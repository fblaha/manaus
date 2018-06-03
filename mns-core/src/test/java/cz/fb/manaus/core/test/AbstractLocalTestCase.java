package cz.fb.manaus.core.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.fb.manaus.spring.ManausProfiles.TEST;


@Configuration
@ComponentScan(
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+Configuration$"),
        basePackages = {"cz.fb.manaus"}
)
class TestConfiguration {
}

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"betfair", TEST})
abstract public class AbstractLocalTestCase {
}
