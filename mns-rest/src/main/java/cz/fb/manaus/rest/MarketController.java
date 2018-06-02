package cz.fb.manaus.rest;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import static java.util.Optional.empty;

@Controller
@Profile(ManausProfiles.DB)
public class MarketController {

    @Autowired
    private MarketDao marketDao;

    @ResponseBody
    @RequestMapping(value = "/markets/{id}", method = RequestMethod.GET)
    public Market getMarket(@PathVariable String id) {
        return marketDao.get(id).get();
    }

    @ResponseBody
    @RequestMapping(value = "/markets", method = RequestMethod.GET)
    public List<Market> getMarkets() {
        return marketDao.getMarkets(Optional.of(new Date()), empty(), OptionalInt.empty());
    }

    @ResponseBody
    @RequestMapping(value = "/markets", method = RequestMethod.POST)
    ResponseEntity<?> addOrUpdateMarket(@RequestBody Market market) {
        validateMarket(market);
        marketDao.saveOrUpdate(market);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(market.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    private void validateMarket(Market market) {
        Objects.requireNonNull(market.getId(), "id==null");
        Preconditions.checkArgument(!market.getRunners().isEmpty(), "runners is empty");
        Objects.requireNonNull(market.getEvent().getOpenDate(), "openDate==null");
    }
}
