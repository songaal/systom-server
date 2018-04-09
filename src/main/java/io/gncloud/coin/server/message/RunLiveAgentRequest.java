package io.gncloud.coin.server.message;

import java.util.List;

public class RunLiveAgentRequest {
    // 저장한 agent의 아이디.
    private String agentId;
    // 전략에 따라 여러 거래소를 접근할수 있으므로, 사용할 거래소를 리스트로 담아준다. 계정에는 거래소의 secret키가 셋팅되어 있어야 한다.
    private List<String> exchangeList;
    // 사용자 비밀숫자 4자리. 거래소별 secret 비밀키 접근시 디코딩을 위해 필요. 저장시에도 동일한 pin 으로 인코딩해서 저장한다.
    private String userPin;

    public RunLiveAgentRequest() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public List<String> getExchangeList() {
        return exchangeList;
    }

    public void setExchangeList(List<String> exchangeList) {
        this.exchangeList = exchangeList;
    }

    public String getUserPin() {
        return userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }

    @Override
    public String toString() {
        return "RunLiveAgentRequest{" +
                "agentId='" + agentId + '\'' +
                ", exchangeList=[" + String.join(", ", exchangeList) +
                "], userPin='" + userPin + '\'' +
                '}';
    }
}
