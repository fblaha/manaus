package cz.fb.manaus.spring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Does not undergo proxy wrapping due to
 * {@code spring.dao.exceptiontranslation.enabled=true }
 * like {@link org.springframework.stereotype.Repository}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface DatabaseComponent {
}

