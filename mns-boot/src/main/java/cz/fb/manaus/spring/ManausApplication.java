package cz.fb.manaus.spring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableAutoConfiguration
public class ManausApplication {

    public static void main(String[] args) {
        var builder = new SpringApplicationBuilder(ManausApplication.class);
        var application = builder.application();
        application.setAdditionalProfiles(ManausProfiles.PRODUCTION_REQUIRED);
        builder.run(args);
    }

}