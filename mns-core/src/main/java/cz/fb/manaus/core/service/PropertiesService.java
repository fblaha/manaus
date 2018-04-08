package cz.fb.manaus.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PropertiesService {

    private final String confUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public PropertiesService(@Value("#{systemEnvironment['MNS_CONF_URL'] ?: 'http://localhost:8080'}") String confUrl,
                             Optional<RestTemplate> restTemplate) {
        this.confUrl = CharMatcher.is('/').trimTrailingFrom(confUrl);
        this.restTemplate = restTemplate.orElseGet(() -> new RestTemplate());
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(restTemplate.getForObject(confUrl + "/{name}", String.class, name));
    }

    public void set(String name, String value, Duration ttl) {
        restTemplate.put(confUrl + "/{name}?ttl={ttl}", value, name, ttl.toMinutes() + "m");
    }

    public Map<String, String> list(Optional<String> prefix) {
        String json = restTemplate.getForObject(confUrl + "?prefix={prefix}", String.class, prefix.orElse(""));
        HashMap<String, String> result = new HashMap<>();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            jsonNode.fields().forEachRemaining(e -> result.put(e.getKey(), e.getValue().asText()));
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Optional<String> prefix) {
        restTemplate.delete(confUrl + "/{prefix}", prefix.orElse(""));
    }

}
