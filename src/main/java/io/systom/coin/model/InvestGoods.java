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
    private Float amount;
    private Integer exchangeKeyId;
    private Date createTime;
    private boolean isInvest;

    public InvestGoods() {}

    public InvestGoods(Integer goodsId, String userId, Float amount, Integer exchangeKeyId) {
        this.goodsId = goodsId;
        this.userId = userId;
        this.amount = amount;
        this.exchangeKeyId = exchangeKeyId;
    }

    public boolean isInvest() {
        return isInvest;
    }

    public void setInvest(boolean invest) {
        isInvest = invest;
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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}