package io.systom.coin.model;

public class Certification {

    private String userId;
    private String uniqueKey;
    private String uniqueInSite;
    private String name;
    private String gender;
    private String birth;
    private String phone;

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
