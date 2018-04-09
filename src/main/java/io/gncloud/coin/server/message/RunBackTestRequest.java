package io.gncloud.coin.server.message;

import io.gncloud.coin.server.model.Task;

/*
 * create joonwoo 2018. 3. 25.
 *
 */
public class RunBackTestRequest {

    private Task task;
    private ExchangeAuth exchangeAuth;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public ExchangeAuth getExchangeAuth() {
        return exchangeAuth;
    }

    public void setExchangeAuth(ExchangeAuth exchangeAuth) {
        this.exchangeAuth = exchangeAuth;
    }

    public static class ExchangeAuth {
        private String exchange;
        private String key;
        private String secret;

        public ExchangeAuth() {
        }

        public ExchangeAuth(String exchange, String key, String secret) {
            this.exchange = exchange;
            this.key = key;
            this.secret = secret;
        }

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

        @Override
        public String toString() {
            return "ExchangeAuth{" +
                    "exchange='" + exchange + '\'' +
                    ", key='" + key + '\'' +
                    ", secret='" + secret + '\'' +
                    '}';
        }
    }
}