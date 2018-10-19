package io.systom.coin.model;

import java.util.Date;

public class UserAttribute {

    private String userId;
    private boolean isPaidUser;
    private boolean isCanceled;
    private int paymentDay;
    private Date dueDate;
    private String usdtWallet;
    private String endDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPaidUser() {
        return isPaidUser;
    }

    public void setPaidUser(boolean paidUser) {
        isPaidUser = paidUser;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public int getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(int paymentDay) {
        this.paymentDay = paymentDay;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getUsdtWallet() {
        return usdtWallet;
    }

    public void setUsdtWallet(String usdtWallet) {
        this.usdtWallet = usdtWallet;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "UserAttribute{" +
                "userId='" + userId + '\'' +
                ", isPaidUser=" + isPaidUser +
                ", isCanceled=" + isCanceled +
                ", paymentDay=" + paymentDay +
                ", dueDate=" + dueDate +
                ", usdtWallet='" + usdtWallet + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
