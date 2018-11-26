package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
public class InvestGoods {
    private Integer id;
    private Integer goodsId;
    private String userId;
    private Float investCash;
    private Integer exchangeKeyId;
    private Date createTime;
    private boolean isPaper;
    private boolean isFinished;
    private Date endTime;

    private Float returnsPct;
    private Float equity;
    private String positions;
    private Float cash;

    public InvestGoods() {}

    public InvestGoods(Integer goodsId, String userId, Float investCash, Integer exchangeKeyId, Boolean isPaper) {
        this.goodsId = goodsId;
        this.userId = userId;
        this.investCash = investCash;
        this.exchangeKeyId = exchangeKeyId;
        this.isPaper = isPaper;
    }

    public static InvestGoods createInvestGoods(Goods goods) {
        String cashUnit = goods.getCashUnit();
        Float investCash = 0f;
        if(cashUnit.equalsIgnoreCase("USDT")){
            //1000달러. 한화로 약 100만원.
            investCash = 1000.0f;
        }else if(cashUnit.equalsIgnoreCase("KRW")){
            //한화 100만원.
            investCash = 1000000.0f;
        }
        return new InvestGoods(goods.getId(), goods.getAuthorId(), investCash, -1, true);
    }

    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }

    public Float getReturnsPct() {
        return returnsPct;
    }

    public void setReturnsPct(Float returnsPct) {
        this.returnsPct = returnsPct;
    }

    public Float getEquity() {
        return equity;
    }

    public void setEquity(Float equity) {
        this.equity = equity;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getExchangeKeyId() {
        return exchangeKeyId;
    }

    public void setExchangeKeyId(Integer exchangeKeyId) {
        this.exchangeKeyId = exchangeKeyId;
    }

    public Float getInvestCash() {
        return investCash;
    }

    public void setInvestCash(Float investCash) {
        this.investCash = investCash;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public void setPaper(boolean paper) {
        isPaper = paper;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "InvestGoods{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", userId='" + userId + '\'' +
                ", investCash=" + investCash +
                ", exchangeKeyId=" + exchangeKeyId +
                ", createTime=" + createTime +
                ", isPaper=" + isPaper +
                ", isFinished=" + isFinished +
                ", endTime=" + endTime +
                '}';
    }
}