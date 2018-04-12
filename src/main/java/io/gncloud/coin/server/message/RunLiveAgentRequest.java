package io.gncloud.coin.server.message;

import java.util.List;

public class RunLiveAgentRequest {
    // 저장한 agent의 아이디.
    private String agentId;
    // 전략에 따라 여러 거래소를 접근할수 있으므로, 사용할 거래소를 리스트로 담아준다. 계정에는 거래소의 secret키가 셋팅되어 있어야 한다.
//    private List<String> exchangeList;

    private Integer exchangeKeyId;

    public RunLiveAgentRequest() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

//    public List<String> getExchangeList() {
//        return exchangeList;
//    }
//
//    public void setExchangeList(List<String> exchangeList) {
//        this.exchangeList = exchangeList;
//    }


    public Integer getExchangeKeyId() {
        return exchangeKeyId;
    }

    public void setExchangeKeyId(Integer exchangeKeyId) {
        this.exchangeKeyId = exchangeKeyId;
    }

    @Override
    public String toString() {
        return "RunLiveAgentRequest{" +
                "agentId='" + agentId + '\'' +
                ", exchangeKeyId=" + exchangeKeyId +
                '}';
    }
}
