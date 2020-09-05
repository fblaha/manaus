package cz.fb.manaus.core.repository

interface Repository<T> {
    fun saveOrUpdate(entity: T)
    fun save(entity: T)
    fun read(id: String): T?
    fun delete(id: String)
    fun list(): List<T>
    fun purge()
}