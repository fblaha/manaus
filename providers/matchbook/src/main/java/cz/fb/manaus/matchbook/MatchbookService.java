package cz.fb.manaus.matchbook;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.matchbook.rest.Offer;
import cz.fb.manaus.matchbook.rest.PlaceOffers;
import cz.fb.manaus.matchbook.rest.PlaceReport;
import cz.fb.manaus.reactor.betting.BetEndpoint;
import cz.fb.manaus.reactor.betting.BetService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

@Service
public class MatchbookService implements BetService {

    static <T> ResponseEntity<T> checkResponse(ResponseEntity<T> responseEntity) {
        HttpStatus statusCode = responseEntity.getStatusCode();
        checkState(statusCode.is2xxSuccessful(), statusCode.getReasonPhrase());
        return responseEntity;
    }

    @Override
    public List<String> placeBets(BetEndpoint endpoint, List<Bet> bets) {
        return placeOrUpdate(endpoint, bets);
    }

    private List<String> placeOrUpdate(BetEndpoint endpoint, List<Bet> bets) {
        PlaceOffers offers = new PlaceOffers();
        offers.setExchangeType("back-lay");
        offers.setOddsType("DECIMAL");
        LinkedList<Offer> items = new LinkedList<>();
        boolean update = bets.stream().allMatch(b -> b.getBetId() != null);
        Map<Long, Integer> indices = new HashMap<>();
        for (int i = 0; i < bets.size(); i++) {
            Bet bet = bets.get(i);
            Offer offer = new Offer();
            if (bet.getBetId() != null) {
                long id = Long.parseLong(bet.getBetId());
                offer.setId(id);
                indices.put(id, i);
            }
            Price requestedPrice = bet.getRequestedPrice();
            offer.setSide(requestedPrice.getSide().name().toLowerCase());
            offer.setOdds(requestedPrice.getPrice());
            offer.setStake(requestedPrice.getAmount());
            offer.setTempId(i);
            offer.setRunnerId(bet.getSelectionId());
            items.add(offer);
        }
        HttpMethod method;
        if (update) {
            method = HttpMethod.PUT;
            checkState(items.stream().noneMatch(offer -> offer.getId() == null));
        } else {
            method = HttpMethod.POST;
            checkState(items.stream().allMatch(offer -> offer.getId() == null));
        }
        offers.setOffers(items);
        String offersUrl = endpoint.getBetUrl().get();

        RestTemplate template = getRestTemplate(endpoint.getAuthToken().get());

        ResponseEntity<PlaceReport> responseEntity = template.exchange(offersUrl, method,
                new HttpEntity<>(offers), PlaceReport.class);
        String result[] = new String[bets.size()];
        PlaceReport report = checkResponse(responseEntity).getBody();
        for (Offer offer : report.getOffers()) {
            int index = update ? indices.get(offer.getId()) : (int) offer.getTempId();
            result[index] = Long.toString(offer.getId());
        }
        return ImmutableList.copyOf(result);
    }

    private RestTemplate getRestTemplate(String sessionToken) {
        TokenCookieHttpRequestFactory requestFactory = new TokenCookieHttpRequestFactory(sessionToken);
        return new RestTemplate(requestFactory);
    }

    @Override
    public List<String> updateBets(BetEndpoint endpoint, List<Bet> bets) {
        return placeOrUpdate(endpoint, bets);
    }

    @Override
    public void cancelBets(BetEndpoint endpoint, List<Bet> bets) {
        RestTemplate template = getRestTemplate(endpoint.getAuthToken().get());
        for (Bet bet : bets) {
            String betUrl = CharMatcher.is('/').trimTrailingFrom(endpoint.getBetUrl().get());
            template.delete(betUrl + "/{offerId}", bet.getBetId());
        }
    }
}
