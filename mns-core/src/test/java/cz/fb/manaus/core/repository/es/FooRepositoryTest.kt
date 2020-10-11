package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.repository.Repository
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.testcontainers.elasticsearch.ElasticsearchContainer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

data class Foo(
        val name: String,
        val description: String
)

class FooRepository(operations: ElasticsearchOperations) :
        Repository<Foo> by ElasticsearchRepository(
                Foo::class.java, operations, { it.name }
        )

class FooRepositoryTest {
    companion object {
        var repository: FooRepository

        init {
            val dockerImageName = "docker.elastic.co/elasticsearch/elasticsearch:7.9.2"
            val container = ElasticsearchContainer(dockerImageName)
            container.start()
            val builder = RestClient.builder(HttpHost("localhost", container.firstMappedPort))
            val client = RestHighLevelClient(builder)
            repository = FooRepository(ElasticsearchRestTemplate(client))
        }
    }

    @Before
    @After
    fun clean() {
        repository.purge()
    }

    @Test
    fun `save - read`() {
        val foo = Foo("test", "test description")
        repository.saveOrUpdate(foo)
        assertEquals(foo, repository.read(foo.name))
    }

    @Test
    fun `read missing`() {
        assertNull(repository.read("missing"))
    }

    @Test
    fun `save - update - read`() {
        val foo = Foo("test", "test description")
        repository.saveOrUpdate(foo)
        val nextFoo = foo.copy(description = "better description")
        repository.saveOrUpdate(nextFoo)
        assertEquals(nextFoo, repository.read(foo.name))
    }

    @Test
    fun `save - delete`() {
        val foo = Foo("test", "test description")
        repository.save(foo)
        assertNotNull(repository.read(foo.name))
        repository.delete(foo.name)
        assertNull(repository.read(foo.name))
    }

    @Test
    fun list() {
        val foo = Foo("test", "test description")
        repository.saveOrUpdate(foo)
        assertEquals(1, repository.list().size)
        repository.delete(foo.name)
        assertTrue(repository.list().isEmpty())
    }
}