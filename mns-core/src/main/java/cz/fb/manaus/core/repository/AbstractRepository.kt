package cz.fb.manaus.core.repository

import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectRepository
import kotlin.reflect.KProperty1


abstract class AbstractRepository<T, U>(
        internal val repository: ObjectRepository<T>,
        val key: KProperty1<T, U>
) {

    fun saveOrUpdate(entity: T) {
        repository.update(entity, true)
    }

    fun save(entity: T) {
        repository.insert(entity)
    }

    fun read(id: String): T? {
        return repository.find(key eq id).firstOrDefault()
    }

    fun delete(id: String) {
        repository.remove(key eq id)
    }

    fun list(): List<T> {
        return repository.find().toList()
    }
}