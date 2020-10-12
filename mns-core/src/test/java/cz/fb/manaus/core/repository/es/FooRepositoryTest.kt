package cz.fb.manaus.core.repository.es

import cz.fb.manaus.core.repository.Repository
import org.junit.Ignore
import org.junit.Test
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
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

@Ignore
class FooRepositoryTest : AbstractElasticsearchTestCase() {

    @Test
    fun `save - read`() {
        val foo = Foo("test", "test description")
        fooRepository.saveOrUpdate(foo)
        assertEquals(foo, fooRepository.read(foo.name))
    }

    @Test
    fun `read missing`() {
        assertNull(fooRepository.read("missing"))
    }

    @Test
    fun `save - update - read`() {
        val foo = Foo("test", "test description")
        fooRepository.saveOrUpdate(foo)
        val nextFoo = foo.copy(description = "better description")
        fooRepository.saveOrUpdate(nextFoo)
        assertEquals(nextFoo, fooRepository.read(foo.name))
    }

    @Test
    fun `save - delete`() {
        val foo = Foo("test", "test description")
        fooRepository.save(foo)
        assertNotNull(fooRepository.read(foo.name))
        fooRepository.delete(foo.name)
        assertNull(fooRepository.read(foo.name))
    }

    @Test
    fun list() {
        val foo = Foo("test", "test description")
        fooRepository.saveOrUpdate(foo)
        assertEquals(1, fooRepository.list().size)
        fooRepository.delete(foo.name)
        assertTrue(fooRepository.list().isEmpty())
    }
}