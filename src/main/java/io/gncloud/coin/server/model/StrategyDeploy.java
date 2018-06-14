package io.gncloud.coin.server.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 4.
 * 
 */
public class StrategyDeploy extends Strategy {

    private String explanation;
    private String description;
    private String isSell;
    private float price;
    private String backtest; /*백테스트 필요 옵션 및 요약 결과*/
    private int sellCount;
    private String isPurchase;

    private Date orderTime;


    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsSell() {
        return isSell;
    }

    public void setIsSell(String isSell) {
        this.isSell = isSell;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getBacktest() {
        return backtest;
    }

    public void setBacktest(String backtest) {
        this.backtest = backtest;
    }

    public int getSellCount() {
        return sellCount;
    }

    public void setSellCount(int sellCount) {
        this.sellCount = sellCount;
    }

    public String getIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(String isPurchase) {
        this.isPurchase = isPurchase;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "StrategyDeploy{" +
                "explanation='" + explanation + '\'' +
                ", description='" + description + '\'' +
                ", isSell=" + isSell +
                ", price=" + price +
                ", backtest='" + backtest + '\'' +
                '}';
    }
}