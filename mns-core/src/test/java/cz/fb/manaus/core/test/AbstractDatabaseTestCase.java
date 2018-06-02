package cz.fb.manaus.core.test;


import cz.fb.manaus.spring.DatabaseTestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.fb.manaus.spring.ManausProfiles.DB;
import static cz.fb.manaus.spring.ManausProfiles.TEST;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@ActiveProfiles({"betfair", TEST, DB})
abstract public class AbstractDatabaseTestCase {

}
