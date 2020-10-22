package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile(ManausProfiles.DB)
class MongoSettledBetRepository(
        operations: MongoOperations
) : MongoOperationsAware<SettledBet> by MongoRepository(
        "id", SettledBet::class.java, operations),
        SettledBetRepository {

    override fun find(
            from: Instant?,
            to: Instant?,
            side: Side?,
            maxResults: Int?,
            asc: Boolean
    ): List<SettledBet> {
        val query = Query()
        maxResults?.let { query.limit(it) }
        side?.let { query.addCriteria(Criteria.where("price.side").isEqualTo(it.name)) }
        if (from != null || to != null) {
            val settledWhere = Criteria.where("settled")
            from?.let { settledWhere.gte(it) }
            to?.let { settledWhere.lte(it) }
            query.addCriteria(settledWhere)
        }
        val sortOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        query.with(Sort.by(sortOrder, "settled"))
        return operations.find(query, SettledBet::class.java)
    }
}
