package cz.fb.manaus.rest;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = MetricsController.class)
public class MetricsControllerTest extends AbstractControllerTest {

    @Test
    public void testMetrics() throws Exception {
        checkResponse("/metrics", "metrics.get", "1");
    }
}