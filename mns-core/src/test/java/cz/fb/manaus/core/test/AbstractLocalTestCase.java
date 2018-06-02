package cz.fb.manaus.core.test;

import cz.fb.manaus.spring.LocalTestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.fb.manaus.spring.ManausProfiles.TEST_PROFILE;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LocalTestConfiguration.class)
@ActiveProfiles({"betfair", TEST_PROFILE})
abstract public class AbstractLocalTestCase {
}
