package cz.fb.manaus.betfair;

import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.betfair.rest.AbstractExecutionReport;
import cz.fb.manaus.betfair.rest.AccountFunds;
import cz.fb.manaus.betfair.rest.AccountStatement;
import cz.fb.manaus.betfair.rest.AccountStatementReport;
import cz.fb.manaus.betfair.rest.CompetitionResult;
import cz.fb.manaus.betfair.rest.EventResult;
import cz.fb.manaus.betfair.rest.EventTypeResult;
import cz.fb.manaus.betfair.rest.MarketBook;
import cz.fb.manaus.betfair.rest.MarketCatalogue;
import cz.fb.manaus.betfair.rest.PlaceExecutionReport;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractRemoteTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.lang3.time.DateUtils.addDays;

public class RestBetfairServiceTest extends AbstractRemoteTestCase {

    @Autowired
    private RestBetfairService service;

    @Test
    public void testListEventTypes() throws Exception {
        List<EventTypeResult> eventTypeResults = service.listEventTypes();
        eventTypeResults.forEach(System.out::println);
        assertTrue(eventTypeResults.size() > 0);
    }

    @Test
    public void testAccountFunds() throws Exception {
        AccountFunds accountFunds = service.getAccountFunds();
        System.out.println("accountFunds = " + accountFunds);
        assertTrue(accountFunds.getAvailableToBetBalance() > 0);
    }

    @Test
    public void testListEvents() throws Exception {
        List<EventResult> eventResults = service.listEvents(Collections.singleton("1"), new Date(), addDays(new Date(), 1));
        eventResults.forEach(System.out::println);
        assertTrue(eventResults.size() > 0);
    }

    @Test
    public void testListCompetitions() throws Exception {
        List<CompetitionResult> competitionResults = service.listCompetitions("1");
        competitionResults.forEach(System.out::println);
        assertTrue(competitionResults.size() > 0);
    }

    @Test
    public void testListMarkets() throws Exception {
        Date from = new Date();
        List<MarketCatalogue> marketCatalogues = service.listMarkets(Collections.singleton("1"),
                Collections.emptySet(), from, addDays(from, 14));
        marketCatalogues.forEach(System.out::println);
        assertTrue(marketCatalogues.size() > 0);
    }

    @Test
    public void testListMarketBooks() throws Exception {
        List<MarketBook> markets = service.listMarketBooks(ImmutableSet.of("1.114987613", "1.114987614", "1.114987615",
                "1.114987616", "1.114987621", "1.114987622"));
        markets.forEach(System.out::println);
        markets.forEach(market -> market.getRunners().forEach(System.out::println));
        assertTrue(markets.size() > 0);
    }

    @Test
    public void testPlaceBet() throws Exception {
        PlaceExecutionReport bet = service.placeBets(singletonList(new Bet(null, "1.123450983", 5026012, new Price(5.07d, 2d, Side.BACK), null, 0d)));
        System.out.println("bet = " + bet);
    }

    @Test
    public void testReplaceBet() throws Exception {
        AbstractExecutionReport bet = service.replaceBets(singletonList(new Bet("45446464", "1.114786454", 1234639l, new Price(2d, 2d, Side.BACK), null, 0d)));
        System.out.println("bet = " + bet);
    }

    @Test
    public void testCancelBet() throws Exception {
        AbstractExecutionReport bet = service.cancelBets(singletonList(new Bet("45333414548", "1.116946265", 1234639l, null, null, 0d)));
        System.out.println("bet = " + bet);
    }

    @Test
    public void testStatement() throws Exception {
        AccountStatementReport statement = service.getAccountStatement(50, 100);
        List<AccountStatement> accountStatement = statement.getAccountStatement();
        accountStatement.forEach(System.out::println);
        assertTrue(accountStatement.size() > 0);
    }

}