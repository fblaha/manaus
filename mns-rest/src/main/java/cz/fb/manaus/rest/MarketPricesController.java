package cz.fb.manaus.rest;

import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.OptionalInt;

@Controller
public class MarketPricesController {

    @Autowired
    private MarketPricesDao marketPricesDao;
    @Autowired
    private MarketDao marketDao;

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/prices", method = RequestMethod.GET)
    public List<MarketPrices> getMarketPrices(@PathVariable String id) {
        return marketPricesDao.getPrices(id);
    }

    @RequestMapping(value = "/markets/{id}/prices", method = RequestMethod.POST)
    public ResponseEntity<?> addMarketPrices(@PathVariable String id, @RequestBody MarketPrices marketPrices) {
        marketDao.get(id).ifPresent(marketPrices::setMarket);
        marketPricesDao.saveOrUpdate(marketPrices);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("")
                .build().toUri();
        return ResponseEntity.created(location).build();
    }

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/prices/{selectionId:\\d+}", method = RequestMethod.GET)
    public List<RunnerPrices> getRunnerPrices(@PathVariable String id, @PathVariable int selectionId) {
        return marketPricesDao.getRunnerPrices(id, selectionId, OptionalInt.empty());
    }
}
