package io.gncloud.coin.server.message;

public class AgentRequestParams {

    public static final String RUN_ACTION = "RUN";
    public static final String STOP_ACTION = "STOP";

    public static final String LIVE_MODE = "LIVE";
    public static final String PAPER_MODE = "PAPER";

    // 거래소 키 아이디값.
    // id 로 거래소명, key, secret을 조회할 수 있다.
    private Integer exchangeKeyId;

    //액션. run, stop
    private String action;


    //모드. live, paper
    private String mode;

    public AgentRequestParams() {
    }

    public Integer getExchangeKeyId() {
        return exchangeKeyId;
    }

    public void setExchangeKeyId(Integer exchangeKeyId) {
        this.exchangeKeyId = exchangeKeyId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "AgentRequestParams{" +
                "  exchangeKeyId=" + exchangeKeyId +
                ", action='" + action + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
