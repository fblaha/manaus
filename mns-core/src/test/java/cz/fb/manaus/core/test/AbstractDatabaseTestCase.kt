package cz.fb.manaus.core.test


import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles.DB
import org.dizitart.no2.objects.filters.ObjectFilters
import org.junit.After
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(DB)
abstract class AbstractDatabaseTestCase : AbstractLocalTestCase() {

    @Autowired
    protected lateinit var betActionRepository: BetActionRepository
    @Autowired
    protected lateinit var settledBetRepository: SettledBetRepository
    @Autowired
    protected lateinit var marketRepository: MarketRepository

    @After
    @Before
    fun clean() {
        marketRepository.repository.remove(ObjectFilters.ALL)
        betActionRepository.repository.remove(ObjectFilters.ALL)
        settledBetRepository.repository.remove(ObjectFilters.ALL)
    }
}
