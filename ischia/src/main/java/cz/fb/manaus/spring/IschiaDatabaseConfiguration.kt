package cz.fb.manaus.spring

import org.springframework.context.annotation.*
import org.springframework.core.type.AnnotatedTypeMetadata

internal class IschiaCondition : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val environment = context.environment
        return environment.acceptsProfiles("ischia") && environment.acceptsProfiles(ManausProfiles.DB)
    }
}


@Configuration
@Conditional(IschiaCondition::class)
@ComponentScan("cz.fb.manaus.ischia")
open class IschiaDatabaseConfiguration
