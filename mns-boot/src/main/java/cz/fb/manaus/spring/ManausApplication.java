package cz.fb.manaus.spring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
        // TODO allow all auto configs
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
public class ManausApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ManausApplication.class).run(args);
    }

}