package io.systom.coin.model;

public class Card {

    private Integer id;
    private String userId;
    private String cardNo;
    private String owner;
    private String month;
    private String year;
    private String birthDate;
    private boolean isDefault;
    private String type;
    private String password2; //비밀번호 앞2자리.

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }


    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", owner='" + owner + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
