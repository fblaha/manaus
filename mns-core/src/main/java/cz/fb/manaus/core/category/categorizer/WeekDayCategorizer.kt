package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.repository.domain.Market
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class WeekDayCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    public override fun getCategoryRaw(market: Market): Set<String> {
        val date = market.event.openDate
        return getCategory(Date.from(date))
    }

    internal fun getCategory(date: Date): Set<String> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val weekDay = SimpleDateFormat("E", Locale.US).format(date).toLowerCase()
        return setOf(weekDay)
    }

    companion object {
        const val PREFIX = "weekDay_"
    }
}
