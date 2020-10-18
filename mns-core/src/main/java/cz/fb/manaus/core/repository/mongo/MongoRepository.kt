package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.repository.Repository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo


interface MongoOperationsAware<T> : Repository<T> {
    val operations: MongoOperations
}

class MongoRepository<T>(
        private val identifier: String,
        private val clazz: Class<T>,
        override val operations: MongoOperations,
        private val ensureId: (T) -> T = { it }
) : MongoOperationsAware<T> {

    override fun save(entity: T): T {
        return operations.save(ensureId(entity))
    }

    override fun read(id: String): T? {
        val query = Query().addCriteria(Criteria.where(identifier).isEqualTo(id))
        return operations.findOne(query, clazz)
    }

    override fun delete(id: String) {
        val query = Query().addCriteria(Criteria.where(identifier).isEqualTo(id))
        operations.remove(query, clazz)
    }

    override fun list(): List<T> {
        return operations.findAll(clazz)
    }

    override fun purge() {
        operations.remove(Query(), clazz)
    }
}