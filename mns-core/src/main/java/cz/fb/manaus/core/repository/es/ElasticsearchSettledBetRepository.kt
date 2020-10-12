package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.core.repository.SettledBetRepository
import org.elasticsearch.index.query.Operator
import org.elasticsearch.index.query.QueryBuilders.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import java.time.Instant


interface ElasticsearchOperationsAware<T> : Repository<T> {
    val operations: ElasticsearchOperations
    val coordinates: IndexCoordinates
}

class ElasticsearchSettledBetRepository(operations: ElasticsearchOperations) :
        ElasticsearchOperationsAware<SettledBet> by ElasticsearchRepository(SettledBet::class.java, operations, { it.id }),
        SettledBetRepository {

    override fun update(settledBet: SettledBet) {
        saveOrUpdate(settledBet)
    }

    override fun find(
            from: Instant?,
            to: Instant?,
            side: Side?,
            maxResults: Int?,
            asc: Boolean
    ): List<SettledBet> {
        val builder = NativeSearchQueryBuilder()
        if (maxResults != null)
            builder.withPageable(PageRequest.of(0, maxResults))
        if (side != null)
            builder.withFilter(matchQuery("side", side))
        if (from != null)
            builder.withFilter(rangeQuery("settled").format("date_time").from(from))
        if (to != null)
            builder.withFilter(rangeQuery("settled").format("date_time").to(to))
        return operations.search(builder.build(), SettledBet::class.java, coordinates)
                .map { it.content }.toList()
    }
}
