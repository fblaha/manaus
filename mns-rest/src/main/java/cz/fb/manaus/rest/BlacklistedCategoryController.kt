package cz.fb.manaus.rest

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@Profile(ManausProfiles.DB)
class BlacklistedCategoryController(
        private val blacklistedCategoryRepository: Repository<BlacklistedCategory>
) {

    val blacklist: List<BlacklistedCategory>
        @ResponseBody
        @RequestMapping(value = ["/blacklist"], method = [RequestMethod.GET])
        get() = blacklistedCategoryRepository.list()

}
