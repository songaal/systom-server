package io.gncloud.coin.server.model;

import java.util.Date;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
public class Algo {

    private String algoId;
    private String userId;
    private String source;
    private Date createTime;

    public String getAlgoId() {
        return algoId;
    }

    public void setAlgoId(String algoId) {
        this.algoId = algoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Algo{" +
                "algoId='" + algoId + '\'' +
                ", userId='" + userId + '\'' +
                ", source='" + source + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}