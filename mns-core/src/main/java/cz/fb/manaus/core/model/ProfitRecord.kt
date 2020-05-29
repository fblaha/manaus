package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import cz.fb.manaus.core.category.Category

@JsonPropertyOrder("category", "profit", "theoreticalProfit", "betProfit", "charge", "avgPrice", "backCount", "layCount", "totalCount")
data class ProfitRecord(
        var category: String,
        var theoreticalProfit: Double,
        var avgPrice: Double,
        var charge: Double,
        var layCount: Int,
        var backCount: Int,
        var coverRate: Double? = null,
        var coverCount: Int = 0) {

    val totalCount: Int
        get() = backCount + layCount

    val betProfit: Double
        get() = profit / totalCount

    val profit: Double
        get() = theoreticalProfit - charge

    companion object {
        fun isAllCategory(input: ProfitRecord): Boolean {
            return Category.parse(input.category).isAll
        }
    }
}
