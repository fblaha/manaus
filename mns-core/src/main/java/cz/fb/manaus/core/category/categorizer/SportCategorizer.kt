package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.util.regex.Pattern


@Component
class SportCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    private fun getCategory(market: Market): String? {
        val typeName = market.eventType.name.toLowerCase()
        when {
            "basketball" == typeName -> return MarketCategories.BASKETBALL
            AMERICAN_FOOTBALL.matcher(typeName).matches() -> return MarketCategories.AMERICAN_FOOTBALL
            ICE_HOCKEY.matcher(typeName).matches() -> return MarketCategories.ICE_HOCKEY
            MOTOR_SPORT.matcher(typeName).matches() -> return MarketCategories.MOTOR_SPORT
            "volleyball" == typeName -> return MarketCategories.VOLLEYBALL
            "soccer" == typeName -> return MarketCategories.SOCCER
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
        val category = getCategory(market)
        return if (category != null) setOf(category) else emptySet()
    }

    companion object {
        val AMERICAN_FOOTBALL = Pattern.compile("american\\s+football")!!
        val MOTOR_SPORT = Pattern.compile("motor\\s+sport")!!
        val ICE_HOCKEY = Pattern.compile("ice\\s+hockey")!!
        const val PREFIX = "sport_"
    }
}
