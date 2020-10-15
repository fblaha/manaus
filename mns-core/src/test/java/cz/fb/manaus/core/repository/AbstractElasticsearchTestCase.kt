package cz.fb.manaus.core.repository

import cz.fb.manaus.core.repository.es.ElasticsearchBetActionRepository
import cz.fb.manaus.core.repository.es.ElasticsearchMarketRepository
import cz.fb.manaus.core.repository.es.ElasticsearchSettledBetRepository
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
        var marketRepository: MarketRepository
        var betActionRepository: BetActionRepository

        init {
            val dockerImageName = "docker.elastic.co/elasticsearch/elasticsearch:7.9.2"
            val container = ElasticsearchContainer(dockerImageName)
            container.start()
            val builder = RestClient.builder(HttpHost("localhost", container.firstMappedPort))
            val client = RestHighLevelClient(builder)
            val operations = ElasticsearchRestTemplate(client)
            fooRepository = FooRepository(operations)
            settledBetRepository = ElasticsearchSettledBetRepository(operations)
            marketRepository = ElasticsearchMarketRepository(operations)
            betActionRepository = ElasticsearchBetActionRepository(operations)
        }
    }

    @Before
    @After
    fun clean() {
        fooRepository.purge()
        settledBetRepository.purge()
        marketRepository.purge()
        betActionRepository.purge()
    }

}
