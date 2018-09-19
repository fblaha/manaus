package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.BoundType
import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.Range.*
import com.google.common.collect.RangeMap
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.SettledBet
import java.time.temporal.ChronoUnit.*
import java.util.*
import java.util.Objects.requireNonNull
import java.util.logging.Level
import java.util.logging.Logger

abstract class AbstractBeforeCategorizer protected constructor(private val category: String) : SettledBetCategorizer {

    val dayMap: RangeMap<Long, String> = ImmutableRangeMap.builder<Long, String>()
            .put(Range.upTo(0L, BoundType.OPEN), category + DAY + NEGATIVE)
            .put(Range.singleton(0L), category + DAY + "0-1")
            .put(Range.singleton(1L), category + DAY + "1-2")
            .put(Range.singleton(2L), category + DAY + "2-3")
            .put(closedOpen(3L, 6L), category + DAY + "3-6")
            .put(downTo(6L, BoundType.CLOSED), category + DAY + "6d+")
            .build()

    private val hourMap: RangeMap<Long, String> = ImmutableRangeMap.builder<Long, String>()
            .put(Range.upTo(0L, BoundType.OPEN), category + HOUR + NEGATIVE)
            .put(Range.singleton(0L), category + HOUR + "0-1")
            .put(Range.singleton(1L), category + HOUR + "1-2")
            .put(Range.singleton(2L), category + HOUR + "2-3")
            .put(closedOpen(3L, 6L), category + HOUR + "3-6")
            .put(closedOpen(6L, 12L), category + HOUR + "6-12")
            .put(downTo(12L, BoundType.CLOSED), category + HOUR + "12-24")
            .build()

    private val minMap: RangeMap<Long, String> = ImmutableRangeMap.builder<Long, String>()
                .put(upTo(0L, BoundType.OPEN), category + MIN + NEGATIVE)
                .put(closedOpen(0L, 10L), category + MIN + "0-10")
                .put(closedOpen(10L, 20L), category + MIN + "10-20")
                .put(closedOpen(20L, 30L), category + MIN + "20-30")
                .put(closedOpen(30L, 40L), category + MIN + "30-40")
                .put(closedOpen(40L, 50L), category + MIN + "40-50")
                .put(downTo(50L, BoundType.CLOSED), category + MIN + "50-60")
                .build()

    protected abstract fun getDate(settledBet: SettledBet): Date?

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val date = getDate(settledBet) ?: return setOf()
        val market = settledBet.betAction.market
        if (date.after(market.event.openDate)) {
            log.log(Level.WARNING, "BEFORE_RESOLVER: ''{0}'' date ''{1}'' after market start  ''{2}''", arrayOf(category, date, market))
        }
        val diffDay = DAYS.between(date.toInstant(), market.event.openDate.toInstant())
        val result = HashSet<String>()
        result.add(requireNonNull<String>(dayMap.get(diffDay)))
        if (diffDay == 0L) {
            handleDay(date, market, result)
        }
        return result
    }

    private fun handleDay(date: Date, market: Market, result: MutableSet<String>) {
        val diffHours = HOURS.between(date.toInstant(), market.event.openDate.toInstant())
        result.add(requireNonNull<String>(hourMap.get(diffHours)))
        if (diffHours == 0L) {
            handleMin(date, market, result)

        }
    }

    private fun handleMin(date: Date, market: Market, result: MutableSet<String>) {
        val diffMin = MINUTES.between(date.toInstant(), market.event.openDate.toInstant())
        result.add(requireNonNull<String>(minMap.get(diffMin)))
    }

    companion object {
        const val NEGATIVE = "<0"
        const val DAY = "_day_"
        const val HOUR = "_hour_"
        const val MIN = "_min_"
        private val log = Logger.getLogger(AbstractBeforeCategorizer::class.java.simpleName)
    }

}
