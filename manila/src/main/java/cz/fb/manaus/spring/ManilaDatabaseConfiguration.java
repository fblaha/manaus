package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

class ManilaCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        return environment.acceptsProfiles("manila")
                && environment.acceptsProfiles(ManausProfiles.DB);
    }
}


@Configuration
@Conditional(ManilaCondition.class)
@ComponentScan("cz.fb.manaus.manila")
public class ManilaDatabaseConfiguration {

}
