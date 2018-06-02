package cz.fb.manaus.spring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
        // TODO remove hibernate
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
public class ManausApplication {

    public static void main(String[] args) {
        var builder = new SpringApplicationBuilder(ManausApplication.class);
        var application = builder.application();
        ManausProfiles.PRODUCTION_REQUIRED.forEach(application::setAdditionalProfiles);
        builder.run(args);
    }

}