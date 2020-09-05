package cz.fb.manaus.core.repository.nitrite

import cz.fb.manaus.core.repository.Repository
import org.dizitart.no2.objects.ObjectRepository

interface RepositoryAware<T> : Repository<T> {
    val repository: ObjectRepository<T>
}