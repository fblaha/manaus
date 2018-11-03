package cz.fb.manaus.spring

import com.google.common.collect.ImmutableSet
import cz.fb.manaus.core.model.*
import org.apache.commons.dbcp2.BasicDataSource
import org.flywaydb.core.Flyway
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.context.annotation.*
import org.springframework.core.env.Environment
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource


@Profile(ManausProfiles.DB)
@Configuration
@EnableTransactionManagement
@ComponentScan("cz.fb.manaus.core")
open class CoreDatabaseConfiguration {

    @Autowired
    @Qualifier("hibernateProperties")
    private val hibernateProperties: Properties? = null

    @Bean(destroyMethod = "close")
    open fun dataSource(@Value(CoreDatabaseConfiguration.JDBC_DRIVER_EL) driver: String,
                        @Value(CoreDatabaseConfiguration.JDBC_URL_EL) url: String,
                        @Value(CoreDatabaseConfiguration.JDBC_USER_EL) username: String?,
                        @Value(CoreDatabaseConfiguration.JDBC_PASSWORD_EL) password: String?): DataSource {
        val dataSource = BasicDataSource()
        dataSource.driverClassName = driver
        dataSource.url = url
        dataSource.username = username
        dataSource.password = password
        dataSource.validationQuery = "select 1"
        return dataSource
    }

    @Bean
    open fun hibernatePropertiesFactory(): PropertiesFactoryBean {
        val bean = PropertiesFactoryBean()
        bean.setProperties(hibernateProperties!!)
        return bean
    }

    @Bean(name = ["hibernateProperties"])
    open fun hibernateProperties(environment: Environment): Properties {
        val properties = Properties()
        val profiles = ImmutableSet.copyOf(environment.activeProfiles)

        if (profiles.contains("external-db")) {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
        } else {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")

            // hibernate.dialect=org.hibernate.dialect.MariaDBDialect
            // hibernate.hbm2ddl.auto=create
            // hibernate.show_sql=true
        }
        return properties
    }

    @Lazy
    @Bean(initMethod = "migrate")
    open fun flyway(dataSource: DataSource): Flyway {
        val flyway = Flyway()
        flyway.isBaselineOnMigrate = true
        flyway.dataSource = dataSource
        return flyway
    }

    @Bean
    @DependsOn("flyway")
    open fun sessionFactory(dataSource: DataSource): LocalSessionFactoryBean {
        val sessionFactory = LocalSessionFactoryBean()
        sessionFactory.setDataSource(dataSource)
        sessionFactory.setAnnotatedClasses(*HIBERNATE_CLASSES)
        sessionFactory.hibernateProperties = hibernateProperties!!
        return sessionFactory
    }

    @Autowired
    @Bean
    open fun transactionManager(sessionFactory: SessionFactory): HibernateTransactionManager {
        return HibernateTransactionManager(sessionFactory)
    }

    companion object {

        const val DEFAULT_DB_URL = "jdbc:h2:mem:manaus;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;MODE=MySQL;INIT=CREATE SCHEMA IF NOT EXISTS manaus"
        const val JDBC_DRIVER_EL = "#{systemEnvironment['MNS_JDBC_DRIVER'] ?: 'org.h2.Driver'}"
        const val JDBC_USER_EL = "#{systemEnvironment['MNS_JDBC_USERNAME']}"
        const val JDBC_PASSWORD_EL = "#{systemEnvironment['MNS_JDBC_PASSWORD']}"
        const val JDBC_URL_EL =
                "#{systemEnvironment['MNS_JDBC_URL'] ?: '$DEFAULT_DB_URL'}"
        val HIBERNATE_CLASSES = arrayOf(Market::class.java, Event::class.java, RunnerPrices::class.java,
                Price::class.java, MarketPrices::class.java, BetAction::class.java, SettledBet::class.java)
    }

}
