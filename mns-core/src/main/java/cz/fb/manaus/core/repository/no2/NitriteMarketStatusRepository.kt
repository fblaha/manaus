package cz.fb.manaus.core.repository.no2

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Nitrite
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class NitriteMarketStatusRepository(db: Nitrite) :
        Repository<MarketStatus> by NitriteRepository(db.getRepository {}, MarketStatus::id)