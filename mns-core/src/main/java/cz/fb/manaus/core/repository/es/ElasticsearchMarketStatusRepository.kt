package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class ElasticsearchMarketStatusRepository(
        operations: ElasticsearchOperations
) : ElasticsearchOperationsAware<MarketStatus> by ElasticsearchRepository(
        MarketStatus::class.java, operations, { it.id }
)
