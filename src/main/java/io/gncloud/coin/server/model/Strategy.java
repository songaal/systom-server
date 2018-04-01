package io.gncloud.coin.server.model;

import java.util.Date;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
public class Strategy {

    private String id;
    private String version;
    private String name;
    private Date createTime;
    private String userId;
    private String code;
    private String options;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "Strategy{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", code='" + code + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}