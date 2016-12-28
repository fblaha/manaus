package cz.fb.manaus.rest;

import cz.fb.manaus.core.service.PropertiesService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;


@ContextConfiguration(classes = PropertiesController.class)
public class PropertiesControllerTest extends AbstractControllerTest {

    @Autowired
    private PropertiesService service;

    @Test
    public void testListProperty() throws Exception {
        service.set("x.y.z", "xyz_value", Duration.ofDays(5));
        checkResponse("/properties/x", "x.y.z", "xyz_value");
    }

}