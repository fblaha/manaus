package cz.fb.manaus.core.provider;

public class ExchangeProvider {

    private final String name;
    private final double minAmount;
    private final double minPrice;
    private final double chargeRate;
    private final boolean perMarketCharge;

    public ExchangeProvider(String name, double minAmount, double minPrice,
                            double chargeRate, boolean perMarketCharge) {
        this.name = name;
        this.minAmount = minAmount;
        this.minPrice = minPrice;
        this.chargeRate = chargeRate;
        this.perMarketCharge = perMarketCharge;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getChargeRate() {
        return chargeRate;
    }

    public boolean isPerMarketCharge() {
        return perMarketCharge;
    }

    public String getName() {
        return name;
    }

}
