package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class ElasticsearchBetActionRepository(
        operations: ElasticsearchOperations
) : ElasticsearchOperationsAware<BetAction> by ElasticsearchRepository(
        BetAction::class.java, operations, { it.id }
),
        BetActionRepository {

    override fun deleteByMarket(marketId: String): Int {
        val list = find(marketId)
        list.forEach { operations.delete(it.id, coordinates) }
        operations.indexOps(coordinates).refresh()
        return list.size
    }

    override fun find(marketId: String): List<BetAction> {
        val query = NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("marketId", marketId))
                .build()
        return operations.search(query, BetAction::class.java, coordinates)
                .map { it.content }.toList()
    }

    override fun setBetId(actionId: String, betId: String): Int {
        val action = operations.get(actionId, BetAction::class.java, coordinates) ?: return 0
        save(action.copy(betId = betId))
        return 1
    }

    override fun findRecentBetAction(betId: String): BetAction? {
        val query = NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("betId", betId))
                .withSort(SortBuilders.fieldSort("time").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 1))
                .build()
        return operations.searchOne(query, BetAction::class.java, coordinates)?.content
    }

    override fun findRecentBetActions(limit: Int): List<BetAction> {
        val query = NativeSearchQueryBuilder()
                .withSort(SortBuilders.fieldSort("time").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, limit))
                .build()
        return operations.search(query, BetAction::class.java, coordinates)
                .map { it.content }.toList()
    }
}