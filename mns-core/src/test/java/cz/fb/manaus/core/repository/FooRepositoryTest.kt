package cz.fb.manaus.core.repository

import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.getRepository
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.junit.Test
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Index(value = "name", type = IndexType.Unique)
data class Foo(
        @Id val name: String,
        val description: String
)


@Component
@Profile(ManausProfiles.DB)
class FooRepository(private val db: Nitrite) :
        AbstractRepository<Foo>({ db.getRepository {} }, Foo::name)

class FooRepositoryTest : AbstractDatabaseTestCase() {

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
        assertTrue(fooRepository.list().isEmpty())
        fooRepository.saveOrUpdate(foo)
        assertEquals(1, fooRepository.list().size)
        fooRepository.delete(foo.name)
        assertTrue(fooRepository.list().isEmpty())
    }
}