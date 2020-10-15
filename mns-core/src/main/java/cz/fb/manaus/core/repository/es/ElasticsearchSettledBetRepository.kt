package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles
import org.elasticsearch.index.query.QueryBuilders.*
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile(ManausProfiles.DB)
class ElasticsearchSettledBetRepository(
        operations: ElasticsearchOperations
) : ElasticsearchOperationsAware<SettledBet> by ElasticsearchRepository(SettledBet::class.java, operations, { it.id }),
        SettledBetRepository {

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

        val boolQuery = boolQuery()
        side?.let { boolQuery.must(matchQuery("side", side)) }
        val range = rangeQuery("settled").format("date_time")
        from?.let { builder.withFilter(range.from(it)) }
        to?.let { builder.withFilter(range.to(it)) }
        builder.withFilter(boolQuery.must(range))
        val sortOrder = if (asc) SortOrder.ASC else SortOrder.DESC
        builder.withSort(SortBuilders.fieldSort("settled").order(sortOrder))
        return operations.search(builder.build(), SettledBet::class.java, coordinates)
                .map { it.content }.toList()
    }
}
