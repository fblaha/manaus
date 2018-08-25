package cz.fb.manaus.core.test


import cz.fb.manaus.spring.ManausProfiles.DB
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(DB)
abstract class AbstractDatabaseTestCase : AbstractLocalTestCase()
