package cz.fb.manaus.core.repository.es

import com.google.common.base.CaseFormat
import cz.fb.manaus.core.repository.Repository
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder
import org.springframework.data.elasticsearch.core.query.Query


interface ElasticsearchOperationsAware<T> : Repository<T> {
    val operations: ElasticsearchOperations
    val coordinates: IndexCoordinates
}

class ElasticsearchRepository<T>(
        private val clazz: Class<T>,
        override val operations: ElasticsearchOperations,
        private val key: (T) -> String
) : ElasticsearchOperationsAware<T> {

    override val coordinates: IndexCoordinates = IndexCoordinates.of(
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.simpleName.toLowerCase())
    )

    override fun save(entity: T): String  {
        val indexQuery = IndexQueryBuilder()
                .withId(key(entity))
                .withObject(entity).build()
        val id = operations.index(indexQuery, coordinates)
        operations.indexOps(coordinates).refresh()
        return id
    }

    override fun read(id: String): T? {
        return operations.get(id, clazz, coordinates)
    }

    override fun delete(id: String) {
        operations.delete(id, coordinates)
        operations.indexOps(coordinates).refresh()
    }

    override fun list(): List<T> {
        return operations.search(Query.findAll(), clazz, coordinates)
                .map { it.content }.toList()
    }

    override fun purge() {
        val indexOps = operations.indexOps(coordinates)
        if (!indexOps.exists()) {
            indexOps.create()
        }
        operations.delete(Query.findAll(), clazz, coordinates)
        indexOps.refresh()
    }
}