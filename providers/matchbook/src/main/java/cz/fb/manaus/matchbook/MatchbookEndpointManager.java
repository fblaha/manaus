package cz.fb.manaus.matchbook;

import org.springframework.stereotype.Component;

@Component
public class MatchbookEndpointManager {

    public static final String REST = "https://matchbook.com/edge/rest/";
    public static final String OLD_REST = "https://matchbook.com/bpapi/rest/";

    public String rest(String path) {
        return REST + path;
    }

    public String oldRest(String path) {
        return OLD_REST + path;
    }

}
