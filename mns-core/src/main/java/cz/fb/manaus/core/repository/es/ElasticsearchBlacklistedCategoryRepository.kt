package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class ElasticsearchBlacklistedCategoryRepository(
        operations: ElasticsearchOperations
) : ElasticsearchOperationsAware<BlacklistedCategory> by ElasticsearchRepository(
        BlacklistedCategory::class.java, operations, { it.name }
)
