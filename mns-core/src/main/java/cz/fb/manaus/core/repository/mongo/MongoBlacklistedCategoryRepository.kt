package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class MongoBlacklistedCategoryRepository(
        operations: MongoOperations
) : MongoOperationsAware<BlacklistedCategory> by MongoRepository(
        "name", BlacklistedCategory::class.java, operations
)
