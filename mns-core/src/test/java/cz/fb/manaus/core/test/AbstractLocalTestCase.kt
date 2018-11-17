package cz.fb.manaus.core.test

import cz.fb.manaus.spring.ManausProfiles.Companion.TEST
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


@Configuration
@ComponentScan(useDefaultFilters = false, includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [Configuration::class])], basePackages = ["cz.fb.manaus.spring"])
open class TestConfiguration

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@ActiveProfiles("betfair", TEST)
abstract class AbstractLocalTestCase
