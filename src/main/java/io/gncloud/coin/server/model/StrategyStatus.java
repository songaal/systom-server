package io.gncloud.coin.server.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 12.
 * 
 */
public class StrategyStatus {

    private Integer id;
    private Integer version;
    private String userId;
    private String status;  /* 사용: use, 미사용: unused */
    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}