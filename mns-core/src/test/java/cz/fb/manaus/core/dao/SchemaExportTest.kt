package cz.fb.manaus.core.dao

import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.spring.CoreDatabaseConfiguration
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.tool.hbm2ddl.SchemaExport
import org.hibernate.tool.schema.TargetType
import org.junit.Test
import java.util.*

class SchemaExportTest : AbstractDatabaseTestCase() {

    @Test
    fun exportDatabaseSchema() {
        val serviceRegistry = StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .build()
        val metadata = MetadataSources(
                serviceRegistry)

        for (hibernateClass in CoreDatabaseConfiguration.HIBERNATE_CLASSES) {
            metadata.addAnnotatedClass(hibernateClass)
        }
        val export = SchemaExport()
        export.create(EnumSet.of(TargetType.STDOUT), metadata.buildMetadata())
    }

}
