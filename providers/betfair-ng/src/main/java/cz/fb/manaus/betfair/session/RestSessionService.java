package cz.fb.manaus.betfair.session;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.net.HostAndPort;
import cz.fb.manaus.betfair.rest.AppVersion;
import cz.fb.manaus.betfair.rest.Application;
import cz.fb.manaus.betfair.rest.LoginResult;
import cz.fb.manaus.betfair.rest.LogoutResult;
import cz.fb.manaus.core.provider.ProviderConfigurationValidator;
import cz.fb.manaus.reactor.traffic.ExpensiveOperationModerator;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.fb.manaus.betfair.RestBetfairService.handleHttpClientException;
import static java.nio.file.Files.newInputStream;

@Service
public class RestSessionService implements ProviderConfigurationValidator {

    public static final String X_APPLICATION = "X-Application";
    public static final String SUCCESS = "SUCCESS";
    public static final ParameterizedTypeReference<List<Application>> APP_LIST = new ParameterizedTypeReference<List<Application>>() {
    };
    private static final Logger log = Logger.getLogger(RestSessionService.class.getSimpleName());
    private final ExpensiveOperationModerator sessionModerator = new ExpensiveOperationModerator(Duration.ofSeconds(20), "betfairSession");
    private final ExpensiveOperationModerator appKeyModerator = new ExpensiveOperationModerator(Duration.ofSeconds(20), "betfairAppKey");

    private final String user;
    private final HostAndPort proxy;
    private final String password;
    private final String keystore;
    private LoadingCache<String, RestSession> sessionCache = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(6, TimeUnit.HOURS)
            .removalListener(this::onRemoval)
            .build(CacheLoader.from(this::buildSession));
    private final Supplier<String> appKey = Suppliers.memoize(this::extractAppKey);

    @Autowired
    public RestSessionService(@Value(CoreLocalConfiguration.USER_EL) String user,
                              @Value(CoreLocalConfiguration.PASSWORD_EL) String password,
                              @Value(CoreLocalConfiguration.KEYSTORE_EL) String keystore,
                              @Value(CoreLocalConfiguration.PROXY_EL) String proxy) {
        this.user = user;
        this.password = password;
        this.keystore = keystore;
        if (Strings.isNullOrEmpty(proxy)) {
            this.proxy = null;
        } else {
            this.proxy = HostAndPort.fromString(proxy);
        }
    }

    private static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }

    void onRemoval(RemovalNotification<String, RestSession> notification) {
        logout(notification.getValue());
    }

    String createSession() {

        RestTemplate restTemplate = getRestTemplate(true);
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_APPLICATION, "manaus");

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        sessionModerator.exceptionOnExceeded();
        ResponseEntity<LoginResult> responseEntity = restTemplate.postForEntity(
                "https://identitysso.betfair.com/api/certlogin?username={username}&password={password}",
                httpEntity, LoginResult.class, user, password);
        LoginResult result = responseEntity.getBody();

        Preconditions.checkState(SUCCESS.equals(result.getLoginStatus()), result.getLoginStatus());

        String token = result.getSessionToken();
        log.log(Level.INFO, "SESSION: new session token");
        return token;
    }

    private RestSession buildSession() {
        return new RestSession(createSession(), getRestTemplate(false));
    }

    public RestSession getCachedSession() {
        try {
            return sessionCache.get("template");
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private RestTemplate getRestTemplate(boolean loadCert) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = null;
            if (loadCert) {
                keyManagers = getKeyManagers("pkcs12", newInputStream(Paths.get(keystore)), "");
            }
            ctx.init(keyManagers, null, new SecureRandom());
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, new DefaultHostnameVerifier());


            HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                    .setSSLContext(ctx)
                    .setSSLSocketFactory(socketFactory);

            if (proxy != null) {
                clientBuilder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort()));
            }
            CloseableHttpClient httpClient = clientBuilder.build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            requestFactory.setConnectTimeout((int) Duration.ofMinutes(2).toMillis());
            requestFactory.setReadTimeout((int) Duration.ofMinutes(2).toMillis());
            return new RestTemplate(requestFactory);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @PreDestroy
    public void logout() {

        RestSession session = getCachedSession();
        logout(session);
    }

    private void logout(RestSession session) {
        RestTemplate restTemplate = session.getTemplate();

        HttpHeaders headers = createCommonHeaders(session.getToken(), false);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<LogoutResult> responseEntity = handleHttpClientException(log,
                () -> restTemplate.postForEntity("https://identitysso.betfair.com/api/logout", httpEntity, LogoutResult.class));
        LogoutResult logoutResult = responseEntity.getBody();
        log.log(Level.INFO, "SESSION: logout status ''{0}'' ", logoutResult.getStatus());
        Preconditions.checkState(SUCCESS.equals(logoutResult.getStatus()), logoutResult.getError());
        Preconditions.checkState(Strings.isNullOrEmpty(logoutResult.getError()), logoutResult.getError());
    }

    public HttpHeaders createCommonHeaders(String token, boolean appkKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Authentication", token);
        headers.add(com.google.common.net.HttpHeaders.CONTENT_TYPE, "application/json");
        if (appkKey) {
            headers.add(X_APPLICATION, getAppKey());
        }
        return headers;
    }

    public String getAppKey() {
        return appKey.get();
    }

    private String extractAppKey() {
        RestSession cachedSession = getCachedSession();
        RestTemplate restTemplate = cachedSession.getTemplate();
        HttpHeaders commonHeaders = createCommonHeaders(cachedSession.getToken(), false);
        HttpEntity<?> httpEntity = new HttpEntity<>(commonHeaders);
        appKeyModerator.exceptionOnExceeded();
        ResponseEntity<List<Application>> entity = handleHttpClientException(log,
                () -> restTemplate.exchange("https://api.betfair.com/exchange/account/rest/v1.0/getDeveloperAppKeys/",
                        HttpMethod.POST,
                        httpEntity, APP_LIST));
        Application application = entity.getBody().get(0);
        Optional<String> delay = application.getAppVersions()
                .stream()
                .filter(appVersion -> !appVersion.getVersion().toLowerCase().contains("delay"))
                .map(AppVersion::getApplicationKey)
                .findAny();
        return delay.get();
    }

    @Override
    public boolean isConfigured() {
        if (!Strings.isNullOrEmpty(user)
                && !Strings.isNullOrEmpty(password)
                && !Strings.isNullOrEmpty(keystore)) {
            Path keyFile = Paths.get(this.keystore);
            return Files.isReadable(keyFile);
        }
        return false;
    }

}
