package cz.fb.manaus.spring

import org.springframework.context.annotation.*
import org.springframework.core.env.Profiles
import org.springframework.core.type.AnnotatedTypeMetadata

internal object ManilaCondition : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val environment = context.environment
        return environment.acceptsProfiles(Profiles.of("manila")) &&
                environment.acceptsProfiles(Profiles.of(ManausProfiles.DB))
    }
}


@Configuration
@Conditional(ManilaCondition::class)
@ComponentScan("cz.fb.manaus.manila")
open class ManilaDatabaseConfiguration
