package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.common.AbstractBestPriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
object BestPriceProposer : AbstractBestPriceProposer(1)

@Component
class BackAdviser(proposers: List<PriceProposer>) : ProposerAdviser(proposers)

@Component
object AcceptAllValidator : Validator {
    override fun validate(context: BetContext): ValidationResult {
        return ValidationResult.ACCEPT
    }
}

@Component
@Profile(ManausProfiles.DB)
class BackBettor(validators: List<Validator>, backAdviser: BackAdviser) : AbstractUpdatingBettor(
        side = Side.BACK,
        validators = validators,
        priceAdviser = backAdviser)

