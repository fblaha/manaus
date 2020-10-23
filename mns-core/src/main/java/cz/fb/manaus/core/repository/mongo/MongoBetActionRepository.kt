package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.bson.types.ObjectId
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

fun ensureId(action: BetAction): BetAction {
    if (action.id.isBlank()) {
        return action.copy(id = ObjectId().toString())
    }
    return action
}

@Component
@Profile(ManausProfiles.DB)
class MongoBetActionRepository(
        operations: MongoOperations
) : MongoOperationsAware<BetAction> by MongoRepository(
        identifier = "id",
        clazz = BetAction::class.java,
        operations = operations,
        ensureId = ::ensureId
), BetActionRepository {

    override fun deleteByMarket(marketId: String): Int {
        val query = Query()
        query.addCriteria(Criteria.where("marketId").isEqualTo(marketId))
        return operations.remove(query, BetAction::class.java).deletedCount.toInt()
    }

    override fun find(marketId: String): List<BetAction> {
        val query = Query()
        query.addCriteria(Criteria.where("marketId").isEqualTo(marketId))
        return operations.find(query, BetAction::class.java)
    }

    override fun setBetId(actionId: String, betId: String): Boolean {
        val action = operations.findById(actionId, BetAction::class.java)
        if (action != null) {
            save(action.copy(betId = betId))
            return true
        }
        return false
    }

    override fun findRecentBetAction(betId: String): BetAction? {
        val query = Query()
        query.addCriteria(Criteria.where("betId").isEqualTo(betId))
        query.with(Sort.by(Sort.Direction.DESC, "time"))
        query.limit(1)
        return operations.findOne(query, BetAction::class.java)
    }

    override fun findRecentBetActions(limit: Int): List<BetAction> {
        val query = Query()
        query.with(Sort.by(Sort.Direction.DESC, "time"))
        query.limit(limit)
        return operations.find(query, BetAction::class.java)
    }
}