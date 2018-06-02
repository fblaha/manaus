package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

class IschiaCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        return environment.acceptsProfiles("ischia")
                && environment.acceptsProfiles(ManausProfiles.DB_PROFILE);
    }
}


@Configuration
@Conditional(IschiaCondition.class)
@ComponentScan("cz.fb.manaus.ischia")
public class IschiaDatabaseConfiguration {

}
