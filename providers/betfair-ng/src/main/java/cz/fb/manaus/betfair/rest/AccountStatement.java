package cz.fb.manaus.betfair.rest;


//refId: "39629399722"
//        itemDate: "2014-08-01T21:13:36.000Z"
//        amount: -0.01
//        balance: 1914.45
//        itemClassData: {
//        unknownStatementItem: "{"avgPrice":0.0,"betSize":0.0,"betType":"B","betCategoryType":"NONE","commissionRate":"9.09%","eventId":114692444,"eventTypeId":300000,"fullMarketName":"Womens Athletics / 100m Hurdles/ Gold Medal Winner","grossBetAmount":0.11,"marketName":"Gold Medal Winner","marketType":"O","placedDate":"2014-07-31T09:18:58.000Z","selectionId":0,"selectionName":null,"startDate":"0001-01-01T00:00:00.000Z","transactionType":"ACCOUNT_DEBIT","transactionId":0,"winLose":"RESULT_NOT_APPLICABLE"}"
//        }-
//        legacyData: {
//        avgPrice: 0
//        betSize: 0
//        betType: "B"
//        betCategoryType: "NONE"
//        commissionRate: "9.09%"
//        eventId: 114692444
//        eventTypeId: 300000
//        fullMarketName: "Womens Athletics / 100m Hurdles/ Gold Medal Winner"
//        grossBetAmount: 0.11
//        marketName: "Gold Medal Winner"
//        marketType: "O"
//        placedDate: "2014-07-31T09:18:58.000Z"
//        selectionId: 0
//        startDate: "0001-01-01T00:00:00.000Z"
//        transactionType: "ACCOUNT_DEBIT"
//        transactionId: 0
//        winLose: "RESULT_NOT_APPLICABLE"
//        }-
//        itemClass: "UNKNOWN"

//        refId
//        String
//
//        itemDate
//        Date
//
//        amount
//        double
//
//        balance
//        double
//
//        itemClass
//        ItemClass
//
//        itemClassData
//        Map<String,String>
//
//        legacyData
//        StatementLegacyData

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class AccountStatement {

    private String refId;
    private Date itemDate;
    private double amount;
    private double balance;
    private ItemClass itemClass;
    @JsonIgnore
    private String itemClassData;
    private StatementLegacyData legacyData;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Date getItemDate() {
        return itemDate;
    }

    public void setItemDate(Date itemDate) {
        this.itemDate = itemDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ItemClass getItemClass() {
        return itemClass;
    }

    public void setItemClass(ItemClass itemClass) {
        this.itemClass = itemClass;
    }

    public String getItemClassData() {
        return itemClassData;
    }

    public void setItemClassData(String itemClassData) {
        this.itemClassData = itemClassData;
    }

    public StatementLegacyData getLegacyData() {
        return legacyData;
    }

    public void setLegacyData(StatementLegacyData legacyData) {
        this.legacyData = legacyData;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("refId", refId)
                .add("itemDate", itemDate)
                .add("amount", amount)
                .add("balance", balance)
                .add("itemClass", itemClass)
                .add("itemClassData", itemClassData)
                .add("legacyData", legacyData)
                .toString();
    }
}
