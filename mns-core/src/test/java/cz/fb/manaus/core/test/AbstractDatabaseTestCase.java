package cz.fb.manaus.core.test;


import org.springframework.test.context.ActiveProfiles;

import static cz.fb.manaus.spring.ManausProfiles.DB;

@ActiveProfiles(DB)
abstract public class AbstractDatabaseTestCase extends AbstractLocalTestCase {

}
