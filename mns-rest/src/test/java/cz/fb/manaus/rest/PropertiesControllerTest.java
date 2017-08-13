package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import cz.fb.manaus.core.model.Property;
import cz.fb.manaus.core.service.PropertiesService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = PropertiesController.class)
public class PropertiesControllerTest extends AbstractControllerTest {

    @Autowired
    private PropertiesService service;

    @Test
    public void testListProperty() throws Exception {
        service.set("x.y.z", "xyz_value", Duration.ofDays(5));
        checkResponse("/properties/x", "x.y.z", "xyz_value");
    }

    @Test
    public void testSetProperty() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Date expiryDate = Date.from(Instant.now().plus(5, ChronoUnit.DAYS));
        Property property = new Property("a.b.c", "x.y.z", expiryDate);
        String serialized = mapper.writer().writeValueAsString(property);
        MvcResult result = mvc.perform(post("/properties")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().is(201))
                .andReturn();
        assertThat(result.getResponse().getHeader(HttpHeaders.LOCATION), notNullValue());
    }
}