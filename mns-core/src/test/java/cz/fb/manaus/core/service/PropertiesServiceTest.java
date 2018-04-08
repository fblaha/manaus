package cz.fb.manaus.core.service;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.dao.AbstractDaoTest;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

// TODO approach to test PropertiesService
public class PropertiesServiceTest extends AbstractDaoTest {

    @Autowired
    private PropertiesService service;

    @Before
    public void assumeRealImplementation() {
        Assume.assumeTrue(PropertiesService.class.equals(service.getClass()));
    }

    @Before
    @After
    public void cleanUpConfiguration() throws Exception {
        service.delete(Optional.empty());
        Preconditions.checkState(service.list(Optional.empty()).isEmpty());
    }

    @Test
    public void testService() throws Exception {
        assertThat(service.list(Optional.empty()).size(), is(0));
        assertThat(service.get("aaa").orElse(null), nullValue());
        service.set("aaa", "BBB", Duration.ofDays(100));
        assertThat(service.get("aaa").get(), is("BBB"));
        assertThat(service.list(Optional.empty()).size(), is(1));
        service.set("aaa", "CCC", Duration.ofDays(100));
        assertThat(service.get("aaa").get(), is("CCC"));
        assertThat(service.list(Optional.empty()).size(), is(1));
        service.set("bbb", "CCC", Duration.ofDays(100));
        assertThat(service.get("aaa").get(), is("CCC"));
        assertThat(service.get("bbb").get(), is("CCC"));
        assertThat(service.list(Optional.empty()).size(), is(2));
    }

    @Test
    public void testListing() throws Exception {
        service.set("aaa", "BBB", Duration.ofDays(100));
        assertThat(service.list(Optional.empty()).size(), is(1));
        assertThat(service.list(Optional.of("aaa")).size(), is(1));
        assertThat(service.list(Optional.of("a")).size(), is(1));
        assertThat(service.list(Optional.of("b")).size(), is(0));
    }

    @Test
    public void testExpiredUpdate() throws Exception {
        service.set("aaa", "XXX", Duration.ofDays(-1));
        assertThat(service.get("aaa").orElse(null), nullValue());
        service.set("aaa", "XXX", Duration.ofDays(1));
        assertThat(service.get("aaa").get(), is("XXX"));
        service.set("aaa", "XXX", Duration.ofDays(-1));
        assertThat(service.get("aaa").orElse(null), nullValue());
    }

    @Test
    public void testDelete() throws Exception {
        service.set("a.b.c", "BBB", Duration.ofDays(100));
        assertThat(service.get("a.b.c").get(), is("BBB"));
        service.delete(Optional.empty());
        assertThat(service.get("a.b.c").orElse(null), nullValue());
        service.set("a.b.c", "BBB", Duration.ofDays(100));
        service.delete(Optional.of("b"));
        assertThat(service.get("a.b.c").get(), is("BBB"));
        service.delete(Optional.of("a."));
        assertThat(service.get("a.b.c").orElse(null), nullValue());
    }
}
