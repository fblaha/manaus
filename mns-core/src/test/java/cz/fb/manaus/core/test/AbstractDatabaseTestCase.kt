package cz.fb.manaus.core.test


import cz.fb.manaus.core.repository.*
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
    @Autowired
    protected lateinit var taskExecutionRepository: TaskExecutionRepository
    @Autowired
    protected lateinit var blacklistedCategoryRepository: BlacklistedCategoryRepository
    @Autowired
    protected lateinit var fooRepository: FooRepository

    @After
    @Before
    fun clean() {
        marketRepository.repository.remove(ObjectFilters.ALL)
        betActionRepository.repository.remove(ObjectFilters.ALL)
        settledBetRepository.repository.remove(ObjectFilters.ALL)
        taskExecutionRepository.repository.remove(ObjectFilters.ALL)
        blacklistedCategoryRepository.repository.remove(ObjectFilters.ALL)
        fooRepository.repository.remove(ObjectFilters.ALL)
    }
}
