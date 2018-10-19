package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
public class Invoice {
    private Integer id;
    private Integer goodsId;
    private String name;
    private Integer investId;
    private String userId;
    private Float initCash;
    private String cashUnit;
    private Float returns;
    private Float paymentPrice;
    private String status;
    private Date paymentTime;
    private Date createTime;
    private Float commissionRate;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Float getInitCash() {
        return initCash;
    }

    public void setInitCash(Float initCash) {
        this.initCash = initCash;
    }

    public Float getReturns() {
        return returns;
    }

    public void setReturns(Float returns) {
        this.returns = returns;
    }

    public Float getPaymentPrice() {
        return paymentPrice;
    }

    public void setPaymentPrice(Float paymentPrice) {
        this.paymentPrice = paymentPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getCashUnit() {
        return cashUnit;
    }

    public void setCashUnit(String cashUnit) {
        this.cashUnit = cashUnit;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Float commissionRate) {
        this.commissionRate = commissionRate;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", name='" + name + '\'' +
                ", investId=" + investId +
                ", initCash=" + initCash +
                ", cashUnit=" + cashUnit +
                ", returns=" + returns +
                ", paymentPrice=" + paymentPrice +
                ", status='" + status + '\'' +
                ", paymentTime=" + paymentTime +
                ", createTime=" + createTime +
                '}';
    }
}