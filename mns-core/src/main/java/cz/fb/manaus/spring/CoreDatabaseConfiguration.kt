package cz.fb.manaus.spring

import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.File


@Profile(ManausProfiles.DB)
@Configuration
@ComponentScan("cz.fb.manaus.core")
open class CoreDatabaseConfiguration {

    @Bean(destroyMethod = "close")
    open fun db(@Value("#{systemEnvironment['MNS_DB_FILE']}") dbFile: String?): Nitrite {
        return if (dbFile == null) {
            nitrite {
                autoCommitBufferSize = 2048
                autoCompact = false
            }
        } else {
            nitrite {
                file = File(dbFile)
                autoCommitBufferSize = 2048
                autoCompact = false
            }
        }
    }

}
