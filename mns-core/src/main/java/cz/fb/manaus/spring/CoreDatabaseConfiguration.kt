package cz.fb.manaus.spring

import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile(ManausProfiles.DB)
@Configuration
@ComponentScan("cz.fb.manaus.core")
open class CoreDatabaseConfiguration {

    @Bean
    open fun db(): Nitrite {
        return nitrite {
            autoCommitBufferSize = 2048
            autoCompact = false
        }
    }

}
