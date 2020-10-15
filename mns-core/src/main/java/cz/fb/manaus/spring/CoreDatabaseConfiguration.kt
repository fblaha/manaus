package cz.fb.manaus.spring

import cz.fb.manaus.spring.conf.DatabaseConf
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate


@Profile(ManausProfiles.DB)
@Configuration
@ComponentScan("cz.fb.manaus.core")
@EnableConfigurationProperties(DatabaseConf::class)
open class CoreDatabaseConfiguration {

    @Bean
    open fun operations(databaseConf: DatabaseConf): ElasticsearchOperations {
        val (host, port) = databaseConf
        val builder = RestClient.builder(HttpHost(host, port))
        val client = RestHighLevelClient(builder)
        return ElasticsearchRestTemplate(client)
    }

}
