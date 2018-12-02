package cz.fb.manaus.spring.conf

import com.google.common.base.Preconditions
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("filter")
data class FilterConf(var runnerName: String?,
                      var marketTypes: List<String>?) {

    private val log = Logger.getLogger(FilterConf::class.java.simpleName)

    @PostConstruct
    fun validate() {
        Preconditions.checkState(!marketTypes.isNullOrEmpty())
        log.info("$this")
    }
}