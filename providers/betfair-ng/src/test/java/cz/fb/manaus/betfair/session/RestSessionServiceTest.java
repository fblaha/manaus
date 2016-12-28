package cz.fb.manaus.betfair.session;

import cz.fb.manaus.core.test.AbstractRemoteTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class RestSessionServiceTest extends AbstractRemoteTestCase {

    @Autowired
    private RestSessionService service;

    @Test
    public void testSession() throws Exception {
        assertNotNull(service.getCachedSession().getToken());
    }

    @Test
    public void testKeys() throws Exception {
        String appKey = service.getAppKey();
        System.out.println("appKey = " + appKey);
        assertThat(appKey, notNullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void testLastLogin() throws Exception {
        service.createSession();
        service.createSession();
    }

}