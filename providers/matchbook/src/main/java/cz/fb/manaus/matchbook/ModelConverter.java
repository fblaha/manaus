package cz.fb.manaus.matchbook;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Competition;
import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.matchbook.rest.Event;
import cz.fb.manaus.matchbook.rest.Market;
import cz.fb.manaus.matchbook.rest.MatchedBet;
import cz.fb.manaus.matchbook.rest.MetaTag;
import cz.fb.manaus.matchbook.rest.Offer;
import cz.fb.manaus.matchbook.rest.Price;
import cz.fb.manaus.matchbook.rest.Runner;
import cz.fb.manaus.matchbook.rest.SettledEvent;
import cz.fb.manaus.matchbook.rest.SettledMarket;
import cz.fb.manaus.matchbook.rest.SettledPage;
import cz.fb.manaus.matchbook.rest.SettledSelection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class ModelConverter {

    private static final Logger log = Logger.getLogger(ModelConverter.class.getSimpleName());

    @Autowired
    private ExchangeProvider provider;


    public cz.fb.manaus.core.model.MarketPrices toModel(Event event, Market market) {
        cz.fb.manaus.core.model.Market modelMarket = new cz.fb.manaus.core.model.Market();

        List<MetaTag> metaTags = event.getMetaTags();
        MetaTag sport = metaTags.stream()
                .filter(metaTag -> "sport".equalsIgnoreCase(metaTag.getType()))
                .findAny().get();
        EventType eventType = new EventType(Long.toString(event.getSportId()), sport.getName());
        modelMarket.setEventType(eventType);
        Optional<MetaTag> competition = metaTags.stream()
                .filter(metaTag -> "competition".equalsIgnoreCase(metaTag.getType()))
                .findAny();
        if (competition.isPresent()) {
            modelMarket.setCompetition(new Competition(Long.toString(competition.get().getId()), competition.get().getName()));
        }
        modelMarket.setName(market.getName());
        modelMarket.setId(Long.toString(market.getId()));
        modelMarket.setBspMarket(false);
        modelMarket.setInPlay(market.isAllowLiveBetting());

        modelMarket.setType(CharMatcher.whitespace().replaceFrom(market.getName().toLowerCase(), '_'));

        cz.fb.manaus.core.model.Event modelEvent = new cz.fb.manaus.core.model.Event(
                Long.toString(event.getId()), event.getName(), event.getStart(), null);
        modelMarket.setEvent(modelEvent);
        LinkedList<cz.fb.manaus.core.model.Runner> runners = new LinkedList<>();
        List<RunnerPrices> runnerPrices = new LinkedList<>();
        int sortPriority = 0;
        for (Runner runner : market.getRunners()) {
            cz.fb.manaus.core.model.Runner modelRunner = new cz.fb.manaus.core.model.Runner();
            modelRunner.setHandicap(runner.getHandicap());
            modelRunner.setName(runner.getName());
            modelRunner.setSelectionId(runner.getId());
            modelRunner.setSortPriority(sortPriority++);
            runners.add(modelRunner);
            List<cz.fb.manaus.core.model.Price> priceList = runner.getPrices()
                    .stream()
                    .map(this::toModelPrice)
                    .collect(Collectors.toList());
            RunnerPrices prices = new RunnerPrices(runner.getId(), priceList, null, null);
            runnerPrices.add(prices);
        }
        modelMarket.setRunners(runners);
        return new MarketPrices(1, modelMarket, runnerPrices);
    }


    public Map<String, SettledBet> toModel(SettledPage page) {
        Map<String, SettledBet> result = new LinkedHashMap<>();
        for (SettledEvent event : page.getEvents()) {
            for (SettledMarket market : event.getMarkets()) {
                for (SettledSelection selection : market.getSelections()) {
                    String selectionId = selection.getId();
                    String selectionName = selection.getName();
                    for (cz.fb.manaus.matchbook.rest.SettledBet bet : selection.getBets()) {
                        String offerId = Long.toString(bet.getOfferId());
                        SettledBet modelBet = toModel(selectionId, selectionName, bet);
                        // TODO handle this correctly
                        if (result.containsKey(offerId)) {
                            log.log(Level.WARNING, "The same offer ID ''{0}'' and ''{1}''",
                                    new Object[]{result.get(offerId), modelBet});
                        }
                        result.put(offerId, modelBet);
                    }
                }
            }
        }
        return result;
    }

    private SettledBet toModel(String selectionId, String selectionName, cz.fb.manaus.matchbook.rest.SettledBet bet) {
        List<String> parsedId = Splitter.on('_').splitToList(selectionId);
        Preconditions.checkState(parsedId.size() == 2);
        SettledBet result = new SettledBet(Long.parseLong(parsedId.get(0)), selectionName, Double.parseDouble(bet.getProfitAndLoss()),
                bet.getSettledTime(), new cz.fb.manaus.core.model.Price(
                bet.getOdds(), bet.getStake(), parseSide(parsedId.get(1))));
        result.setMatched(bet.getMatchedTime());
        return result;
    }


    private cz.fb.manaus.core.model.Price toModelPrice(Price price) {
        return new cz.fb.manaus.core.model.Price(price.getDecimalOdds(),
                price.getAvailableAmount(),
                parseSide(price.getSide()));
    }

    private Side parseSide(String side) {
        return Side.valueOf(side.toUpperCase());
    }

    public Bet toModel(Offer offer) {
        return new Bet(Long.toString(offer.getId()),
                Long.toString(offer.getMarketId()),
                offer.getRunnerId(),
                new cz.fb.manaus.core.model.Price(offer.getDecimalOdds(),
                        // fixes returned price requestedPrice=1.507614,1.999999
                        Math.max(offer.getStake(), provider.getMinAmount()),
                        parseSide(offer.getSide())),
                offer.getCreatedAt(), getMatchedAmount(offer.getMatchedBets()));
    }

    private double getMatchedAmount(List<MatchedBet> bets) {
        if (bets == null || bets.isEmpty()) {
            return 0;
        } else {
            return bets.stream().mapToDouble(MatchedBet::getStake).sum();
        }
    }
}
