package io.gncloud.coin.server.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 12.
 * 
 */
public class StrategyOrder {

    private Integer id;
    private Integer version;
    private String userId;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}