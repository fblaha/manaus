package cz.fb.manaus.spring

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@ComponentScan("cz.fb.manaus.reactor")
@Profile(ManausProfiles.DB)
open class ReactorDatabaseConfiguration
