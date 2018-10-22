package io.systom.coin.model;

import io.systom.coin.service.InvoiceService;

import java.util.Date;

public class MembershipInvoice extends Invoice {

    private String name;
    private String customerUid;
    private String merchantUid;
    private String paymentImpUid;
    private String paymentResult;
    private boolean isWait;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPaymentResult() {
        return paymentResult;
    }

    @Override
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

    @Override
    public String toString() {
        return "MembershipInvoice{" +
                "name='" + name + '\'' +
                ", customerUid='" + customerUid + '\'' +
                ", merchantUid='" + merchantUid + '\'' +
                ", paymentImpUid='" + paymentImpUid + '\'' +
                ", paymentResult='" + paymentResult + '\'' +
                ", isWait=" + isWait +
                '}';
    }
}
