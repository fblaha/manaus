package cz.fb.manaus.rest;

import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.OptionalInt;

@Controller
@Profile(ManausProfiles.DB_PROFILE)
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

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/prices/{selectionId:\\d+}", method = RequestMethod.GET)
    public List<RunnerPrices> getRunnerPrices(@PathVariable String id, @PathVariable int selectionId) {
        return marketPricesDao.getRunnerPrices(id, selectionId, OptionalInt.empty());
    }
}
