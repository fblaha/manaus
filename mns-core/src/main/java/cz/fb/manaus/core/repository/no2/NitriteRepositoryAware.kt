package cz.fb.manaus.core.repository.no2

import cz.fb.manaus.core.repository.Repository
import org.dizitart.no2.objects.ObjectRepository

interface NitriteRepositoryAware<T> : Repository<T> {
    val repository: ObjectRepository<T>
}