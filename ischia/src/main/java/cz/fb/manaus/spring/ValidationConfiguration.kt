package cz.fb.manaus.spring

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.reflect.full.findAnnotation

@Configuration
@Profile("ischia")
open class ValidationConfiguration {

    @Bean
    @LayLoserBet
    open fun layValidationCoordinator(validationService: ValidationService, @LayLoserBet validators: List<Validator>): ValidationCoordinator {
        validators.forEach { checkNotNull(it::class.findAnnotation<LayLoserBet>()) }
        check(validators.isNotEmpty())
        return ValidationCoordinator(validators, validationService)

    }

    @Bean
    @BackLoserBet
    open fun backValidationCoordinator(validationService: ValidationService, @BackLoserBet validators: List<Validator>): ValidationCoordinator {
        validators.forEach { checkNotNull(it::class.findAnnotation<BackLoserBet>()) }
        check(validators.isNotEmpty())
        return ValidationCoordinator(validators, validationService)
    }

}
