package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
public class Invoice {
    private Integer id;
    private Integer goodsId;
    private Integer investId;
    private Float initCash;
    private Float returns;
    private Float paymentPrice;
    private String status;
    private Date paymentTime;
    private Date createTime;

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

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", investId=" + investId +
                ", initCash=" + initCash +
                ", returns=" + returns +
                ", paymentPrice=" + paymentPrice +
                ", status='" + status + '\'' +
                ", paymentTime=" + paymentTime +
                ", createTime=" + createTime +
                '}';
    }
}