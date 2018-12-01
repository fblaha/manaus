package cz.fb.manaus.spring

import cz.fb.manaus.spring.conf.DatabaseConf
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.File


@Profile(ManausProfiles.DB)
@Configuration
@ComponentScan("cz.fb.manaus.core")
@EnableConfigurationProperties(DatabaseConf::class)
open class CoreDatabaseConfiguration {


    @Bean(destroyMethod = "close")
    open fun db(databaseConf: DatabaseConf): Nitrite {
        return if (databaseConf.file.isNullOrBlank()) {
            nitrite {
                autoCommitBufferSize = 2048
                autoCompact = false
            }
        } else {
            val db = nitrite {
                file = File(databaseConf.file)
                autoCommitBufferSize = 2048
                autoCompact = false
            }
            db.compact()
            db
        }
    }

}
