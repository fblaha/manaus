package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.test.AbstractDatabaseTestCase;
import cz.fb.manaus.spring.CoreDatabaseConfiguration;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;

import java.util.EnumSet;

public class SchemaExportTest extends AbstractDatabaseTestCase {

    @Test
    public void exportDatabaseSchema() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .build();
        MetadataSources metadata = new MetadataSources(
                serviceRegistry);

        for (Class hibernateClass : CoreDatabaseConfiguration.HIBERNATE_CLASSES) {
            metadata.addAnnotatedClass(hibernateClass);
        }
        SchemaExport export = new SchemaExport();
        export.create(EnumSet.of(TargetType.STDOUT), metadata.buildMetadata());
    }

}
