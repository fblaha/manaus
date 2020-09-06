package cz.fb.manaus.core.repository.no2

import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import kotlin.reflect.KProperty1


class NitriteRepository<T, U>(
    override val repository: ObjectRepository<T>,
    private val key: KProperty1<T, U>
) : NitriteRepositoryAware<T> {

    override fun saveOrUpdate(entity: T) {
        repository.update(entity, true)
    }

    override fun save(entity: T) {
        repository.insert(entity)
    }

    override fun read(id: String): T? {
        return repository.find(key eq id).firstOrDefault()
    }

    override fun delete(id: String) {
        repository.remove(key eq id)
    }

    override fun list(): List<T> {
        return repository.find().toList()
    }

    override fun purge() {
        repository.remove(ObjectFilters.ALL)
    }
}