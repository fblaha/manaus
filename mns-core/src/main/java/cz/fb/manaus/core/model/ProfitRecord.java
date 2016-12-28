package cz.fb.manaus.core.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;
import cz.fb.manaus.core.category.Category;

@JsonPropertyOrder({"category", "profit", "theoreticalProfit", "betProfit", "charge",
        "avgPrice", "backCount", "layCount", "totalCount"})
public class ProfitRecord {

    private String category;
    private double theoreticalProfit;
    private double avgPrice;
    private double charge;
    private int layCount;
    private int backCount;
    private Double coverDiff;
    private int coverCount = 0;

    public ProfitRecord(String category, double theoreticalProfit, int layCount, int backCount, double avgPrice, double charge) {
        this.category = category;
        this.theoreticalProfit = theoreticalProfit;
        this.layCount = layCount;
        this.backCount = backCount;
        this.avgPrice = avgPrice;
        this.charge = charge;
    }

    public ProfitRecord() {
    }

    public static boolean isAllCategory(ProfitRecord input) {
        return Category.parse(input.getCategory()).isAll();
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public double getTheoreticalProfit() {
        return theoreticalProfit;
    }

    public String getCategory() {
        return category;
    }

    public int getLayCount() {
        return layCount;
    }

    public int getBackCount() {
        return backCount;
    }

    public int getTotalCount() {
        return backCount + layCount;
    }

    public double getBetProfit() {
        return getProfit() / getTotalCount();
    }

    public double getCharge() {
        return charge;
    }

    public double getProfit() {
        return theoreticalProfit - charge;
    }

    public int getCoverCount() {
        return coverCount;
    }

    public void setCoverCount(int coverCount) {
        this.coverCount = coverCount;
    }


    public double getCoverRate() {
        return getCoverCount() / (double) getTotalCount();
    }


    public Double getCoverDiff() {
        return coverDiff;
    }

    public void setCoverDiff(Double coverDiff) {
        this.coverDiff = coverDiff;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("category", category)
                .add("theoreticalProfit", theoreticalProfit)
                .add("avgPrice", avgPrice)
                .add("charge", charge)
                .add("layCount", layCount)
                .add("backCount", backCount)
                .add("coverDiff", coverDiff)
                .add("coverCount", coverCount)
                .toString();
    }
}
