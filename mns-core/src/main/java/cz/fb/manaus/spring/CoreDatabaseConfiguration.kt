package cz.fb.manaus.spring

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import cz.fb.manaus.spring.conf.DatabaseConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoTemplate


interface DatabaseInitializer {
    val url: String
}

@Profile(ManausProfiles.DB)
@Configuration
@ComponentScan("cz.fb.manaus.core")
@EnableConfigurationProperties(DatabaseConf::class)
open class CoreDatabaseConfiguration {

    @Bean
    open fun mongoClient(databaseInitializer: DatabaseInitializer?, databaseConf: DatabaseConf): MongoClient {
        val url = databaseInitializer?.url ?: databaseConf.url
        val mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(url))
                .build()
        return MongoClients.create(mongoClientSettings)
    }

    @Bean
    open fun mongoTemplate(client: MongoClient): MongoTemplate {
        return MongoTemplate(client, "test")
    }

}
