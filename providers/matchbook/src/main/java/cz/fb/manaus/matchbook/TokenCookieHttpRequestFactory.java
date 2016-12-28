package cz.fb.manaus.matchbook;

import com.google.common.net.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class TokenCookieHttpRequestFactory extends SimpleClientHttpRequestFactory {


    public static final String USER_AGENT = "curl/7.43.0";
    private final String sessionToken;

    public TokenCookieHttpRequestFactory(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        super.prepareConnection(connection, httpMethod);
        connection.setRequestProperty("session-token", sessionToken);
        connection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT);
    }
}
