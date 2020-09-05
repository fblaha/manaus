package cz.fb.manaus.spring

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
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
    @LayUniverse
    open fun layValidationCoordinator(
            validationService: ValidationService,
            @LayUniverse validators: List<Validator>
    ): ValidationCoordinator {
        validators.forEach { checkNotNull(it::class.findAnnotation<LayUniverse>()) }
        check(validators.isNotEmpty())
        return ValidationCoordinator(validators, validationService)
    }

    @Bean
    @BackUniverse
    open fun backValidationCoordinator(
            validationService: ValidationService,
            @BackUniverse validators: List<Validator>
    ): ValidationCoordinator {
        validators.forEach { checkNotNull(it::class.findAnnotation<BackUniverse>()) }
        check(validators.isNotEmpty())
        return ValidationCoordinator(validators, validationService)
    }

}
