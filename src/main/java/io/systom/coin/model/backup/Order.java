//package io.gncloud.coin.server.model;
//
///*
// * create joonwoo 2018. 4. 16.
// *
// */
//public class Order {
//
//    /*
//    * {
//         "id": "b00597cd281e41f2b9f5b2aa9ec0362b",
//         "timestamp": 1515556800000000000,
//         "exchange": "poloniex",
//         "coin": "btc",
//         "base": "bts",
//         "amount": 1,
//         "price": 0.00004305,
//         "fee": 0
//         }
//    * */
//
//    private Integer id;
//    private Long timestamp;
//    private String exchange;
//    private String coin;
//    private String base;
//    private Double amount;
//    private Double price;
//    private Double fee;
//
//    private Integer agentId;
//    private String action;
//    private String orderType;
//    private String exchangeOrderId;
//    private String description;
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Long timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public String getExchange() {
//        return exchange;
//    }
//
//    public void setExchange(String exchange) {
//        this.exchange = exchange;
//    }
//
//    public String getCoinUnit() {
//        return coin;
//    }
//
//    public void setCoinUnit(String coin) {
//        this.coin = coin;
//    }
//
//    public String getBase() {
//        return base;
//    }
//
//    public void setBase(String base) {
//        this.base = base;
//    }
//
//    public Double getCash() {
//        return amount;
//    }
//
//    public void setCash(Double amount) {
//        this.amount = amount;
//    }
//
//    public Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(Double price) {
//        this.price = price;
//    }
//
//    public Double getFee() {
//        return fee;
//    }
//
//    public void setFee(Double fee) {
//        this.fee = fee;
//    }
//
//    public Integer getAgentId() {
//        return agentId;
//    }
//
//    public void setAgentId(Integer agentId) {
//        this.agentId = agentId;
//    }
//
//    public String getAction() {
//        return action;
//    }
//
//    public void setAction(String action) {
//        this.action = action;
//    }
//
//    public String getOrderType() {
//        return orderType;
//    }
//
//    public void setOrderType(String orderType) {
//        this.orderType = orderType;
//    }
//
//    public String getExchangeOrderId() {
//        return exchangeOrderId;
//    }
//
//    public void setExchangeOrderId(String exchangeOrderId) {
//        this.exchangeOrderId = exchangeOrderId;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    @Override
//    public String toString() {
//        return "Order{" +
//                "id='" + id + '\'' +
//                ", timestamp=" + timestamp +
//                ", exchange='" + exchange + '\'' +
//                ", coin='" + coin + '\'' +
//                ", base='" + base + '\'' +
//                ", amount=" + amount +
//                ", price=" + price +
//                ", fee=" + fee +
//                ", agentId=" + agentId +
//                ", action='" + action + '\'' +
//                ", orderType='" + orderType + '\'' +
//                ", exchangeOrderId='" + exchangeOrderId + '\'' +
//                ", description='" + description + '\'' +
//                '}';
//    }
//}