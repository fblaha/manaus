package cz.fb.manaus.spring

import cz.fb.manaus.spring.conf.BettingConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@ComponentScan("cz.fb.manaus.reactor")
@Profile(ManausProfiles.DB)
@EnableConfigurationProperties(BettingConf::class)
open class ReactorDatabaseConfiguration