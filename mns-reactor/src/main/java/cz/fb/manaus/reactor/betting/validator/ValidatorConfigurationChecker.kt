package cz.fb.manaus.reactor.betting.validator

import com.google.common.base.Preconditions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class ValidatorConfigurationChecker {

    @Autowired(required = false)
    private val validators = mutableListOf<Validator>()

    @PostConstruct
    fun checkValidators() {
        validators.forEach { this.checkConfiguration(it) }
    }

    private fun checkConfiguration(validator: Validator) {
        if (validator.isDowngradeAccepting) {
            Preconditions.checkState(validator.isPriceRequired, "downgrade accepting while price not required")
        }
    }

}
