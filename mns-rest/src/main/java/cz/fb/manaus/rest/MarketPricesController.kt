package cz.fb.manaus.rest

import cz.fb.manaus.core.dao.MarketDao
import cz.fb.manaus.core.dao.MarketPricesDao
import cz.fb.manaus.core.model.MarketPrices
import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
@Profile(ManausProfiles.DB)
class MarketPricesController {

    @Autowired
    private lateinit var marketPricesDao: MarketPricesDao
    @Autowired
    private lateinit var marketDao: MarketDao

    @ResponseBody
    @RequestMapping(value = ["/markets/{id}/prices"], method = [RequestMethod.GET])
    fun getMarketPrices(@PathVariable id: String): List<MarketPrices> {
        return marketPricesDao.getPrices(id)
    }

    @ResponseBody
    @RequestMapping(value = ["/markets/{id}/prices/{selectionId:\\d+}"], method = [RequestMethod.GET])
    fun getRunnerPrices(@PathVariable id: String, @PathVariable selectionId: Int): List<RunnerPrices> {
        return marketPricesDao.getRunnerPrices(id, selectionId.toLong(), OptionalInt.empty())
    }
}
