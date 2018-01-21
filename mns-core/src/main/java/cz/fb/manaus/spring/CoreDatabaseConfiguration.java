package cz.fb.manaus.spring;

import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@ComponentScan("cz.fb.manaus.core")
public class CoreDatabaseConfiguration {

    public static final String DEFAULT_DB_URL =
            "jdbc:h2:mem:manaus;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;MODE=MySQL;INIT=CREATE SCHEMA IF NOT EXISTS manaus";
    public static final String JDBC_DRIVER_EL =
            "#{systemEnvironment['MNS_JDBC_DRIVER'] ?: 'org.h2.Driver'}";
    public static final String JDBC_USER_EL =
            "#{systemEnvironment['MNS_JDBC_USERNAME']}";
    public static final String JDBC_PASSWORD_EL =
            "#{systemEnvironment['MNS_JDBC_PASSWORD']}";
    public static final String JDBC_URL_EL =
            "#{systemEnvironment['MNS_JDBC_URL'] ?: '" + DEFAULT_DB_URL + "'}";
    public static final Class[] HIBERNATE_CLASSES = {
            Market.class, Event.class, RunnerPrices.class, Price.class,
            MarketPrices.class, BetAction.class, SettledBet.class
    };

    @Autowired
    @Qualifier("hibernateProperties")
    private Properties hibernateProperties;

    @Bean(destroyMethod = "close")
    public DataSource dataSource(@Value(CoreDatabaseConfiguration.JDBC_DRIVER_EL) String driver,
                                 @Value(CoreDatabaseConfiguration.JDBC_URL_EL) String url,
                                 @Value(CoreDatabaseConfiguration.JDBC_USER_EL) String username,
                                 @Value(CoreDatabaseConfiguration.JDBC_PASSWORD_EL) String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        return dataSource;
    }

    @Bean
    public PropertiesFactoryBean hibernatePropertiesFactory() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setProperties(hibernateProperties);
        return bean;
    }

    @Bean(name = "hibernateProperties")
    public Properties hibernateProperties(Environment environment) {
        Properties properties = new Properties();
        ImmutableSet<String> profiles = ImmutableSet.copyOf(environment.getActiveProfiles());

        // cache stats
        if (profiles.contains("cache-stats")) {
            properties.setProperty("hibernate.cache.use_structured_entries", "true");
            properties.setProperty("hibernate.generate_statistics", "true");
        }
        if (profiles.contains("external-db")) {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
        } else {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

            // hibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect
            // hibernate.hbm2ddl.auto=create
            // hibernate.show_sql=true
        }
        // cache related
        properties.setProperty("hibernate.cache.region.factory_class",
                "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        return properties;
    }

    @Lazy
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource);
        return flyway;
    }

    @Bean
    @DependsOn("flyway")
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setAnnotatedClasses(HIBERNATE_CLASSES);
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

    @Autowired
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

}
