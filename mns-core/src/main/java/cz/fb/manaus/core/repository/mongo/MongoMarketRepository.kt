package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.time.Instant


@Component
@Profile(ManausProfiles.DB)
class MongoMarketRepository(
        operations: MongoOperations
) : MongoOperationsAware<Market> by MongoRepository(
        "id", Market::class.java, operations
), MarketRepository {

    private val openDate = "event.openDate"

    override fun delete(olderThan: Instant): Int {
        val query = Query()
        query.addCriteria(Criteria.where(openDate).lte(olderThan))
        return operations.remove(query, Market::class.java).deletedCount.toInt()
    }

    override fun find(from: Instant?, to: Instant?, maxResults: Int?): List<Market> {
        val query = buildQuery(from, to, maxResults)
        return operations.find(query, Market::class.java)
    }

    private fun buildQuery(
            from: Instant? = null,
            to: Instant? = null,
            maxResults: Int? = null
    ): Query {
        val query = Query()
        maxResults?.let { query.limit(it) }
        if (from != null || to != null) {
            val settledWhere = Criteria.where(openDate)
            from?.let { settledWhere.gte(it) }
            to?.let { settledWhere.lte(it) }
            query.addCriteria(settledWhere)
        }
        query.with(Sort.by(openDate))
        return query
    }

    override fun findIDs(from: Instant?, to: Instant?): List<String> {
        return find(from = from, to = to).map { it.id }
    }

}