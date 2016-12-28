package cz.fb.manaus.betfair.session;

import org.springframework.web.client.RestTemplate;

public class RestSession {

    private final String token;
    private final RestTemplate template;

    public RestSession(String token, RestTemplate template) {
        this.token = token;
        this.template = template;
    }

    public String getToken() {
        return token;
    }

    public RestTemplate getTemplate() {
        return template;
    }
}
