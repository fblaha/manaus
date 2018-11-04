package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class BackLoserBettor @BackLoserBet @Autowired constructor(
        validators: List<Validator>,
        coordinator: BackLoserAdviser) : AbstractUpdatingBettor(Side.BACK, validators, coordinator)
