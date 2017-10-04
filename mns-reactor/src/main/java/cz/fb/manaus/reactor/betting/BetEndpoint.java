package cz.fb.manaus.reactor.betting;

import java.util.Optional;

@Deprecated
public class BetEndpoint {
    private final Optional<String> betUrl;
    private final Optional<String> authToken;


    public BetEndpoint(Optional<String> betUrl, Optional<String> authToken) {
        this.betUrl = betUrl;
        this.authToken = authToken;
    }

    public static BetEndpoint devNull() {
        return new BetEndpoint(Optional.empty(), Optional.empty());
    }

    public Optional<String> getBetUrl() {
        return betUrl;
    }

    public Optional<String> getAuthToken() {
        return authToken;
    }
}