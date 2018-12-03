package cz.fb.manaus.manila

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class BestChanceLayBettor @ManilaBet @Autowired
constructor(validators: List<Validator>, adviser: BestChanceLayAdviser) :
        AbstractUpdatingBettor(
                side = Side.LAY,
                validators = validators,
                priceAdviser = adviser)
