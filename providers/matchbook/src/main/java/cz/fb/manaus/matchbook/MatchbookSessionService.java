package cz.fb.manaus.matchbook;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.net.HostAndPort;
import com.google.common.net.HttpHeaders;
import cz.fb.manaus.core.provider.ProviderConfigurationValidator;
import cz.fb.manaus.matchbook.rest.Credentials;
import cz.fb.manaus.reactor.traffic.ExpensiveOperationModerator;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static cz.fb.manaus.matchbook.MatchbookService.checkResponse;

@Service
public class MatchbookSessionService implements ProviderConfigurationValidator {

    public static final String REST = "https://matchbook.com/edge/rest/";
    public static final ParameterizedTypeReference<Map<String, ?>> STRING_MAP = new ParameterizedTypeReference<Map<String, ?>>() {
    };
    private final ExpensiveOperationModerator sessionModerator = new ExpensiveOperationModerator(Duration.ofSeconds(10), "session");

    private final String user;
    private final String password;
    private final Optional<HostAndPort> proxy;

    private final LoadingCache<String, RestTemplate> session = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(6, TimeUnit.HOURS)
            .removalListener(this::onSessionExpired)
            .build(CacheLoader.from(this::getFreshTemplate));


    @Autowired
    public MatchbookSessionService(@Value(CoreLocalConfiguration.USER_EL) String user,
                                   @Value(CoreLocalConfiguration.PASSWORD_EL) String password,
                                   @Value(CoreLocalConfiguration.PROXY_EL) String proxy) {
        this.user = user;
        this.password = password;
        if (Strings.isNullOrEmpty(proxy)) {
            this.proxy = Optional.empty();
        } else {
            this.proxy = Optional.of(HostAndPort.fromString(proxy));
        }
    }

    void onSessionExpired(RemovalNotification<String, RestTemplate> notification) {
        RestTemplate value = notification.getValue();
        logout(value);
    }

    String createSession(String user, String password, Optional<HostAndPort> proxy) {
        RestTemplate template = getProxyTemplate(new SimpleClientHttpRequestFactory(), proxy);
        Credentials credentials = new Credentials(user, password);

        RequestEntity<Credentials> request = new RequestEntity<>(credentials, createCommonHeaders(), HttpMethod.POST,
                URI.create(REST + "security/session"));

        sessionModerator.exceptionOnExceeded();
        ResponseEntity<Map<String, ?>> resp = checkResponse(template.exchange(request, STRING_MAP));
        return (String) resp.getBody().get("session-token");
    }

    private LinkedMultiValueMap<String, String> createCommonHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set(HttpHeaders.USER_AGENT, TokenCookieHttpRequestFactory.USER_AGENT);
        return headers;
    }

    @PreDestroy
    public void logout() {
        RestTemplate template = getTemplate();
        logout(template);
    }

    private void logout(RestTemplate template) {
        RequestEntity<Object> entity = new RequestEntity<>(null, createCommonHeaders(), HttpMethod.DELETE,
                URI.create(REST + "security/session"));
        checkResponse(template.exchange(entity, STRING_MAP));
    }

    public RestTemplate getTemplate() {
        try {
            return session.get("template");
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private RestTemplate getFreshTemplate() {
        return getProxyTemplate(new TokenCookieHttpRequestFactory(createSession(user, password, proxy)), proxy);
    }

    private RestTemplate getProxyTemplate(SimpleClientHttpRequestFactory requestFactory, Optional<HostAndPort> proxy) {
        if (proxy.isPresent()) {
            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxy.get().getHost(), proxy.get().getPort())));
        }
        return new RestTemplate(requestFactory);
    }

    @Override
    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(password);
    }
}
