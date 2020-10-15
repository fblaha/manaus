package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.elasticsearch.index.query.QueryBuilders.rangeQuery
import org.elasticsearch.search.sort.SortBuilders
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Component
import java.time.Instant


@Component
@Profile(ManausProfiles.DB)
class ElasticsearchMarketRepository(
        operations: ElasticsearchOperations
) : ElasticsearchOperationsAware<Market> by ElasticsearchRepository(Market::class.java, operations, { it.id }),
        MarketRepository {

    private val openDate = "event.openDate"

    override fun delete(olderThan: Instant): Int {
        // TODO native qy delete
        val old = find(to = olderThan)
        old.forEach { operations.delete(it.id, coordinates) }
        operations.indexOps(coordinates).refresh()
        return old.count()
    }

    override fun find(from: Instant?, to: Instant?, maxResults: Int?): List<Market> {
        val query = buildQuery(from, to, maxResults)
        return operations.search(query, Market::class.java, coordinates)
                .map { it.content }.toList()
    }

    private fun buildQuery(
            from: Instant? = null,
            to: Instant? = null,
            maxResults: Int? = null
    ): NativeSearchQuery {
        val builder = NativeSearchQueryBuilder()
        if (maxResults != null)
            builder.withPageable(PageRequest.of(0, maxResults))
        val range = rangeQuery(openDate).format("date_time")
        from?.let { range.from(it) }
        to?.let { range.to(it) }
        builder.withFilter(range)

        builder.withSort(SortBuilders.fieldSort(openDate))
        return builder.build()
    }

    override fun findIDs(from: Instant?, to: Instant?): List<String> {
        return find(from = from, to = to).map { it.id }
    }

}