package cz.fb.manaus.core.service;

import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkState;
import static cz.fb.manaus.spring.CoreLocalConfiguration.PRODUCTION_PROFILE;

@DatabaseComponent
@Profile(PRODUCTION_PROFILE)
public class TimeZoneChecker {

    public static final String TIMEZONE = "timezone";
    @Autowired
    private PropertiesService service;


    @PostConstruct
    public void checkTimeZone() {
        String runtime = TimeZone.getDefault().getID();
        Optional<String> database = service.get(TIMEZONE);
        if (database.isPresent()) {
            checkState(Objects.equals(database.get(), runtime),
                    "Timezone in database is '%s' while runtime is '%s'", database.get(), runtime);
        } else {
            service.set(TIMEZONE, runtime, Duration.ofDays(365));
        }
    }

}
