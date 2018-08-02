package io.systom.coin.model;

/*
 * create joonwoo 2018. 8. 2.
 * 
 */
public class ForgotPassword {
    public enum ACTIONS {confirm, reset};

    private String action;
    private String userId;
    private String confirmCode;
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConfirmCode() {
        return confirmCode;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}