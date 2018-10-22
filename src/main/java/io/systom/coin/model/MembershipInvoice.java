package io.systom.coin.model;

import io.systom.coin.service.InvoiceService;

import java.util.Date;

public class MembershipInvoice extends Invoice {

    private String customerUid;
    private String merchantUid;
    private String paymentImpUid;
    private String paymentResult;
    private boolean isWait;
    private Date nextPaymentTime;

    public String getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(String paymentResult) {
        this.paymentResult = paymentResult;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public String getMerchantUid() {
        return merchantUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getPaymentImpUid() {
        return paymentImpUid;
    }

    public void setPaymentImpUid(String paymentImpUid) {
        this.paymentImpUid = paymentImpUid;
    }

    public boolean isWait() {
        return isWait;
    }

    public void setWait(boolean wait) {
        isWait = wait;
    }

    public Date getNextPaymentTime() {
        return nextPaymentTime;
    }

    public void setNextPaymentTime(Date nextPaymentTime) {
        this.nextPaymentTime = nextPaymentTime;
    }

    @Override
    public String toString() {
        return "MembershipInvoice{" +
                ", customerUid='" + customerUid + '\'' +
                ", merchantUid='" + merchantUid + '\'' +
                ", paymentImpUid='" + paymentImpUid + '\'' +
                ", paymentResult='" + paymentResult + '\'' +
                ", isWait=" + isWait +
                ", nextPaymentTime=" + nextPaymentTime +
                '}';
    }
}
