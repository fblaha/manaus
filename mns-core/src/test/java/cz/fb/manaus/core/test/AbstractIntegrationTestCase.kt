package cz.fb.manaus.core.test


import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.*
import cz.fb.manaus.spring.ManausProfiles.DB
import org.junit.After
import org.junit.Assume.assumeFalse
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import java.io.IOException
import java.net.Socket


@ActiveProfiles(DB)
abstract class AbstractIntegrationTestCase : AbstractTestCase() {

    @Autowired
    protected lateinit var betActionRepository: BetActionRepository

    @Autowired
    protected lateinit var settledBetRepository: SettledBetRepository

    @Autowired
    protected lateinit var marketRepository: MarketRepository

    @Autowired
    protected lateinit var taskExecutionRepository: Repository<TaskExecution>

    @Autowired
    protected lateinit var blacklistedCategoryRepository: Repository<BlacklistedCategory>

    @Autowired
    protected lateinit var marketStatusRepository: Repository<MarketStatus>

    @Autowired
    protected lateinit var fooRepository: Repository<Foo>

    @Autowired
    private lateinit var repositories: List<Repository<*>>


    companion object {
        @BeforeClass
        @JvmStatic
        fun checkElasticPort() {
            fun available(port: Int): Boolean {
                try {
                    Socket("localhost", port).use { return false }
                } catch (ignored: IOException) {
                    return true
                }
            }
            assumeFalse(available(9200))
        }
    }

    @After
    @Before
    fun clean() {
        repositories.forEach { it.purge() }
    }
}
