package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 20.
 * 
 */
public class Strategy {

    private Integer id;
    private String userId;
    private String name;
    private Date createTime;
    private Date updateTime;
    private String code;

//    전략 목록
    private Integer lastDeployVersion;

    public Strategy() {}

    public Strategy(Integer id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public Strategy(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLastDeployVersion() {
        return lastDeployVersion;
    }

    public void setLastDeployVersion(Integer lastDeployVersion) {
        this.lastDeployVersion = lastDeployVersion;
    }
}