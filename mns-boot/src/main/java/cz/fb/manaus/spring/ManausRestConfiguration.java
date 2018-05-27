package cz.fb.manaus.spring;

import cz.fb.manaus.rest.BetActionController;
import cz.fb.manaus.rest.MaintenanceController;
import cz.fb.manaus.rest.MarketController;
import cz.fb.manaus.rest.MarketPricesController;
import cz.fb.manaus.rest.MarketSnapshotController;
import cz.fb.manaus.rest.MetricsController;
import cz.fb.manaus.rest.ProfitController;
import cz.fb.manaus.rest.SettledBetController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Import({ProfitController.class, MarketController.class,
        MarketPricesController.class, MetricsController.class,
        MarketSnapshotController.class, SettledBetController.class,
        BetActionController.class, MaintenanceController.class})
public class ManausRestConfiguration {

}
