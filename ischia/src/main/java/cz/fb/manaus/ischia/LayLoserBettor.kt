package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class LayLoserBettor @LayLoserBet @Autowired constructor(
        validationCoordinator: ValidationCoordinator, adviser: PriceAdviser) :
        AbstractUpdatingBettor(
                side = Side.LAY,
                validationCoordinator = validationCoordinator,
                priceAdviser = adviser)
