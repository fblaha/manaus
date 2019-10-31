package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.reactor.betting.BetEvent
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
class ValidatorConfigurationChecker(private val validators: List<Validator<BetEvent>> = emptyList()) {

    private val log = Logger.getLogger(ValidatorConfigurationChecker::class.simpleName)

    @PostConstruct
    fun checkValidators() {
        validators.forEach { this.checkConfiguration(it) }
    }

    private fun checkConfiguration(validator: Validator<BetEvent>) {
        log.info { "checking validator '${validator.name}'" }
        if (validator.isDowngradeAccepting) {
            check(validator.isPriceRequired) { "downgrade accepting while price not required" }
        }
    }
}
