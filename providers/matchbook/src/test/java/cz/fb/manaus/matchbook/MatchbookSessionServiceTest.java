package cz.fb.manaus.matchbook;

import cz.fb.manaus.core.test.AbstractRemoteTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static cz.fb.manaus.spring.CoreLocalConfiguration.TEST_PROFILE;
import static junit.framework.TestCase.assertNotNull;

@ActiveProfiles(value = {"matchbook", TEST_PROFILE}, inheritProfiles = false)
public class MatchbookSessionServiceTest extends AbstractRemoteTestCase {

    @Autowired
    private MatchbookSessionService service;

    @Test
    public void testSession() throws Exception {
        assertNotNull(service.getTemplate());
        service.logout();
    }
}