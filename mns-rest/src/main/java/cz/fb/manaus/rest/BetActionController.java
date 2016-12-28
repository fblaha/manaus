package cz.fb.manaus.rest;

import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.model.BetAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static java.util.Optional.empty;

@Controller
public class BetActionController {

    @Autowired
    private BetActionDao betActionDao;

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/actions", method = RequestMethod.GET)
    public List<BetAction> getBetActions(@PathVariable String id) {
        List<BetAction> actions = betActionDao.getBetActions(id, OptionalLong.empty(), empty());
        betActionDao.fetchMarketPrices(actions.stream());
        return actions;
    }

    @ResponseBody
    @RequestMapping(value = "/actions", method = RequestMethod.GET)
    public List<BetAction> getBetActions(@RequestParam(defaultValue = "20") int maxResults) {
        List<BetAction> actions = betActionDao.getBetActions(OptionalInt.of(maxResults));
        betActionDao.fetchMarketPrices(actions.stream());
        return ImmutableList.copyOf(actions).reverse();
    }

}
