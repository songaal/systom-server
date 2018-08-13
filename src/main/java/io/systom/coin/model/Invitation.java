package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 8. 10.
 * 
 */
public class Invitation {

    private Integer id;
    private String userId;
    private String refCode;
    private String refUserId;
    private Boolean status;
    private Date createTime;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getRefUserId() {
        return refUserId;
    }

    public void setRefUserId(String refUserId) {
        this.refUserId = refUserId;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", refCode='" + refCode + '\'' +
                ", refUserId='" + refUserId + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}