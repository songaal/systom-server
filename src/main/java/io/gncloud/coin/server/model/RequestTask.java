package io.gncloud.coin.server.model;

import java.util.List;

/*
 * create joonwoo 2018. 3. 25.
 * 
 */
public class RequestTask {

    private Task task;
    private List<ExchangeAuth> exchangeAuths;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<ExchangeAuth> getExchangeAuths() {
        return exchangeAuths;
    }

    public void setExchangeAuths(List<ExchangeAuth> exchangeAuths) {
        this.exchangeAuths = exchangeAuths;
    }

    public static class ExchangeAuth {
        private String exchange;
        private String key;
        private String secret;

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}