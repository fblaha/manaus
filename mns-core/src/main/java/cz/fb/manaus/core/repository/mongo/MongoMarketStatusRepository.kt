package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class MongoMarketStatusRepository(
        operations: MongoOperations
) : MongoOperationsAware<MarketStatus> by MongoRepository(
        "id", MarketStatus::class.java, operations
)
