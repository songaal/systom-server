package io.systom.coin.model;

import java.util.Date;

public class DualSignal {
    private String type;
    private CoinSignal coin_signal;
    private CoinSignal base_signal;
    private String time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CoinSignal getCoin_signal() {
        return coin_signal;
    }

    public void setCoin_signal(CoinSignal coin_signal) {
        this.coin_signal = coin_signal;
    }

    public CoinSignal getBase_signal() {
        return base_signal;
    }

    public void setBase_signal(CoinSignal base_signal) {
        this.base_signal = base_signal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DualSignal{" +
                "type='" + type + '\'' +
                ", coin_signal=" + coin_signal +
                ", base_signal=" + base_signal +
                ", time='" + time + '\'' +
                '}';
    }
}
