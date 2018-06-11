package io.gncloud.coin.server.model;

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