package io.systom.coin.model;

import java.util.Date;

public class Certification {

    private String userId;
    private String impUid;
    private String uniqueKey;
    private String uniqueInSite;
    private String name;
    private String gender;
    private String birth;
    private String phone;
    private String merchantUid;
    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMerchantUid() {
        return merchantUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getImpUid() {
        return impUid;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getUniqueInSite() {
        return uniqueInSite;
    }

    public void setUniqueInSite(String uniqueInSite) {
        this.uniqueInSite = uniqueInSite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Certification{" +
                "userId='" + userId + '\'' +
                ", uniqueKey='" + uniqueKey + '\'' +
                ", uniqueInSite='" + uniqueInSite + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birth='" + birth + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
