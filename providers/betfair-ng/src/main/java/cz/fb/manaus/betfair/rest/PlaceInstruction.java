package cz.fb.manaus.betfair.rest;


import com.google.common.base.MoreObjects;

public class PlaceInstruction {

    private OrderType orderType;
    private long selectionId;
    private double handicap;
    private Side side;
    private LimitOrder limitOrder;

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public LimitOrder getLimitOrder() {
        return limitOrder;
    }

    public void setLimitOrder(LimitOrder limitOrder) {
        this.limitOrder = limitOrder;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderType", orderType)
                .add("selectionId", selectionId)
                .add("handicap", handicap)
                .add("side", side)
                .add("limitOrder", limitOrder)
                .toString();
    }
}
