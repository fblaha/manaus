package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.reactor.betting.makeName
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
class ValidatorConfigurationChecker(private val validators: List<Validator> = emptyList()) {

    private val log = Logger.getLogger(ValidatorConfigurationChecker::class.simpleName)

    @PostConstruct
    fun checkValidators() {
        validators.forEach { this.checkConfiguration(it) }
    }

    private fun checkConfiguration(validator: Validator) {
        log.info { "checking validator '${makeName(validator)}'" }
    }
}
