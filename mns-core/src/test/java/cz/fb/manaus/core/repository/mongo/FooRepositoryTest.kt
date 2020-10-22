package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import cz.fb.manaus.spring.ManausProfiles
import org.junit.Test
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.stereotype.Component
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


data class Foo(
        @MongoId
        val name: String,
        val description: String
)

@Component
@Profile(ManausProfiles.DB)
class FooRepository(operations: MongoOperations) :
        Repository<Foo> by MongoRepository(
                "name", Foo::class.java, operations
        )

class FooRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun `save - read`() {
        val foo = Foo("test", "test description")
        fooRepository.save(foo)
        assertEquals(foo, fooRepository.read(foo.name))
    }

    @Test
    fun `read missing`() {
        assertNull(fooRepository.read("missing"))
    }

    @Test
    fun `save - update - read`() {
        val foo = Foo("test", "test description")
        fooRepository.save(foo)
        val nextFoo = foo.copy(description = "better description")
        fooRepository.save(nextFoo)
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
        fooRepository.save(foo)
        val list = fooRepository.list()
        assertEquals(1, list.size)
        fooRepository.delete(foo.name)
        assertTrue(fooRepository.list().isEmpty())
    }
}