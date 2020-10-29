package cz.fb.manaus.rest

import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@Profile(ManausProfiles.DB)
class MarketStatusController(
        private val repository: Repository<MarketStatus>
) {

    val statuses: List<MarketStatus>
        @ResponseBody
        @RequestMapping(value = ["/statuses"], method = [RequestMethod.GET])
        get() = repository.list().sortedBy { it.lastEvent }

}
