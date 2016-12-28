package cz.fb.manaus.rest;

import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static java.util.Optional.empty;

@Controller
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
}
