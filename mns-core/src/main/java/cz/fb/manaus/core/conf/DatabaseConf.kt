package cz.fb.manaus.core.conf

import com.google.common.base.Preconditions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
@ConfigurationProperties("db")
data class DatabaseConf(var marketHistoryDays: Long = -1,
                        var file: String? = null) {

    private val log = Logger.getLogger(DatabaseConf::class.java.simpleName)

    @PostConstruct
    fun validate() {
        Preconditions.checkState(marketHistoryDays > 0)
        log.info("Database conf : $this")
    }
}
