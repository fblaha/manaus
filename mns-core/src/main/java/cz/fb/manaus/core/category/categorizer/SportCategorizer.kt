package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.util.regex.Pattern

const val SPORT_PREFIX = "sport_"

@Component
object SportCategorizer : AbstractDelegatingCategorizer(SPORT_PREFIX) {

    private val americanFootball = Pattern.compile("american\\s+football")!!
    private val motorSport = Pattern.compile("motor\\s+sport")!!
    private val iceHockey = Pattern.compile("ice\\s+hockey")!!

    private fun getCategory(market: Market): String? {
        val typeName = market.eventType.name.toLowerCase()
        when {
            "basketball" == typeName -> return MarketCategories.BASKETBALL
            americanFootball.matcher(typeName).matches() -> return MarketCategories.AMERICAN_FOOTBALL
            iceHockey.matcher(typeName).matches() -> return MarketCategories.ICE_HOCKEY
            motorSport.matcher(typeName).matches() -> return MarketCategories.MOTOR_SPORT
            "volleyball" == typeName -> return MarketCategories.VOLLEYBALL
            "soccer" == typeName -> return MarketCategories.SOCCER
            "esports" == typeName -> return MarketCategories.ESPORTS
            "table-tennis" == typeName -> return MarketCategories.TABLE_TENNIS
            "snooker" == typeName -> return MarketCategories.SNOOKER
            "cricket" == typeName -> return MarketCategories.CRICKET
            "handball" == typeName -> return MarketCategories.HANDBALL
            typeName.startsWith("greyhound") -> return MarketCategories.GREY_HOUNDS
            "cycling" == typeName -> return MarketCategories.CYCLING
            "baseball" == typeName -> return MarketCategories.BASEBALL
            "golf" == typeName -> return MarketCategories.GOLF
            "tennis" == typeName -> return MarketCategories.TENNIS
            typeName.startsWith("horse") -> return MarketCategories.HORSES
            typeName.startsWith("financial") -> return MarketCategories.FINANCIAL
            typeName.startsWith("rugby") -> return MarketCategories.RUGBY
            typeName.startsWith("politics") -> return MarketCategories.POLITICS
            else -> return null
        }
    }

    public override fun getCategoryRaw(market: Market): Set<String> {
        return when (val category = getCategory(market)) {
            null -> emptySet()
            else -> setOf(category)
        }
    }

}
