package cz.fb.manaus.spring

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import cz.fb.manaus.spring.conf.DatabaseConf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration


interface DatabaseInitializer {
    val url: String
}

@Profile(ManausProfiles.DB)
@Configuration
@ComponentScan("cz.fb.manaus.core")
@EnableConfigurationProperties(DatabaseConf::class)
open class CoreDatabaseConfiguration : AbstractMongoClientConfiguration() {

    @Autowired
    var databaseInitializer: DatabaseInitializer? = null

    @Autowired
    lateinit var databaseConf: DatabaseConf

    override fun autoIndexCreation(): Boolean = true

    override fun mongoClient(): MongoClient {
        val url = databaseInitializer?.url ?: databaseConf.url
        val mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(url))
                .build()
        return MongoClients.create(mongoClientSettings)
    }

    override fun getDatabaseName(): String = "mns"

}
