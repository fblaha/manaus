package cz.fb.manaus.matchbook;

import com.google.common.base.CharMatcher;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ModelConverter {

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

        modelMarket.setType(CharMatcher.WHITESPACE.replaceFrom(market.getName().toLowerCase(), '_'));

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


    public SettledBet toModel(cz.fb.manaus.matchbook.rest.SettledBet bet) {
        return new SettledBet(bet.getRunnerId(), bet.getRunnerName(), Double.parseDouble(bet.getProfitAndLoss()),
                bet.getPlacedAt(), bet.getSettledAt(), new cz.fb.manaus.core.model.Price(
                Double.parseDouble(bet.getOdds()), Double.parseDouble(bet.getStake()), null));
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
