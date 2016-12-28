package cz.fb.manaus.rest;

import cz.fb.manaus.core.model.Property;
import cz.fb.manaus.core.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Optional.ofNullable;

@Controller
public class PropertiesController {


    @Autowired
    private PropertiesService propertiesService;

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
        propertiesService.delete(checkNotNull(emptyToNull(prefix), "prefix"));
    }

}
