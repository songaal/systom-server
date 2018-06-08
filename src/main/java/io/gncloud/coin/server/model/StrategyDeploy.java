package io.gncloud.coin.server.model;

/*
 * create joonwoo 2018. 6. 4.
 * 
 */
public class StrategyDeploy extends Strategy {

    private String explanation;
    private String description;
    private boolean isSell;
    private float price;
    private String backtest; /*백테스트 필요 옵션 및 요약 결과*/

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

    public boolean isSell() {
        return isSell;
    }

    public void setSell(boolean sell) {
        isSell = sell;
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
}