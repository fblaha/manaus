package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.spring.DatabaseInitializer
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@Component
@Profile(ManausProfiles.DB)
object MongoContainerInitializer : DatabaseInitializer {

    override val url: String by lazy {
        val container = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
        container.start()
        "mongodb://localhost:${container.firstMappedPort}/test"
    }
}