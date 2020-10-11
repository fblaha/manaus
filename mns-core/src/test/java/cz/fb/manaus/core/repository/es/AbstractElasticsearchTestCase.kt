package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.repository.SettledBetRepository
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.junit.After
import org.junit.Before
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.testcontainers.elasticsearch.ElasticsearchContainer

abstract class AbstractElasticsearchTestCase {
    companion object {
        var fooRepository: FooRepository
        var settledBetRepository: SettledBetRepository

        init {
            val dockerImageName = "docker.elastic.co/elasticsearch/elasticsearch:7.9.2"
            val container = ElasticsearchContainer(dockerImageName)
            container.start()
            val builder = RestClient.builder(HttpHost("localhost", container.firstMappedPort))
            val client = RestHighLevelClient(builder)
            val operations = ElasticsearchRestTemplate(client)
            fooRepository = FooRepository(operations)
            settledBetRepository = ElasticsearchSettledBetRepository(operations)
        }
    }

    @Before
    @After
    fun clean() {
        fooRepository.purge()
        settledBetRepository.purge()
    }

}
