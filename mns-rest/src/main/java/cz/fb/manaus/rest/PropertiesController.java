package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.model.Property;
import cz.fb.manaus.core.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Controller
public class PropertiesController {

    @Autowired
    private PropertiesService propertiesService;
    @Autowired
    private MetricRegistry metricRegistry;

    @ResponseBody
    @RequestMapping(value = "/properties", method = RequestMethod.GET)
    public List<Property> getProperties() {
        return getProperties(null);
    }

    @ResponseBody
    @RequestMapping(value = "/properties/{prefix}", method = RequestMethod.GET)
    public List<Property> getProperties(@PathVariable String prefix) {
        return propertiesService.list(ofNullable(emptyToNull(prefix)));
    }

    @ResponseBody
    @RequestMapping(value = "/properties/{prefix}", method = RequestMethod.DELETE)
    public void deleteProperties(@PathVariable String prefix) {
        propertiesService.delete(requireNonNull(emptyToNull(prefix), "prefix"));
    }

    @ResponseBody
    @RequestMapping(value = "/properties", method = RequestMethod.POST)
    public ResponseEntity<?> setProperty(@RequestBody Property property) {
        metricRegistry.counter("property.post").inc();
        propertiesService.set(property);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{name}")
                .buildAndExpand(property.getName()).toUri();
        return ResponseEntity.created(location).build();
    }
}
