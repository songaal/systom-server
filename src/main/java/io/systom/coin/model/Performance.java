package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 20.
 * 
 */
public class Performance {

    private Integer id;
    private Date createTime;
    private float totalEquity; //총 평가 base
    private float totalEquity_usd; //총 평가 usd
    private float totalCommission; //총 수수료
    private float returnPct; //수익률
    private float maxReturnPct; //최고수익
    private float winsPct; //승률
    private int winsCount;


}