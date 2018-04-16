package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Agent;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 4. 13.
 * 
 */
@Service
public class AgentService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AgentService.class);
    @Autowired
    private SqlSession sqlSession;


    public Agent insertAgent(Agent agent) throws OperationException, ParameterException {
        try {
            isNotNull(agent.getStrategyId(), "strategyId");
            isNotNull(agent.getStrategyVersion(), "strategyVersion");
            isNotNull(agent.getBaseCurrency(), "baseCurrency");
            isNotNull(agent.getCapitalBase(), "capitalBase");
            isNotNull(agent.getName(), "agentName");
            isNotNull(agent.getUserId(), "userId");

            if (!agent.isSimulationOrder()) {
                isNotNull(agent.getExchangeKeyId(), "exchangeKeyId");
            }
            agent.setState(Agent.STATE_STOP);
            int resultCount = sqlSession.insert("agent.insertAgent", agent);
            if (resultCount != 1) {
                logger.error("[FAIL] Insert Agent resultCount: {}", resultCount);
                throw new OperationException("[FAIL] Insert Agent resultCount: " + resultCount);
            }
            return getAgent(agent.getId());
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] Agent Insert error");
        }
    }

    public Agent updateAgent(Agent agent) throws OperationException, ParameterException {
        try {
            isNotNull(agent.getId(), "id");
            int resultCount = sqlSession.update("agent.updateAgent", agent);
            if (resultCount != 1) {
                throw new OperationException("[FAIL] Update Agent resultCount: " + resultCount);
            }
            return agent;
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] Agent Update error");
        }
    }

    public List<Agent> selectAgent(String userId) throws OperationException, ParameterException {
        try {
            isNotNull(userId, "userId");
            Agent tmp = new Agent();
            tmp.setUserId(userId);
            List<Agent> agentList = sqlSession.selectList("agent.selectAgent", tmp);
            return agentList == null ? new ArrayList<>() : agentList;
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] Select Agent error");
        }
    }

    public Agent getAgent(Integer agentId) throws OperationException, ParameterException {
        try {
            isNotNull(agentId, "agentId");
            Agent tmp = new Agent();
            tmp.setId(agentId);
            return sqlSession.selectOne("agent.selectAgent", tmp);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] get Agent error");
        }
    }

    public Agent deleteAgent(Agent agent) throws ParameterException, OperationException, AuthenticationException {
        try {
            isNotNull(agent.getId(), "agentId");
            Agent registerAgent = getAgent(agent.getId());
            if (!registerAgent.getUserId().equals(agent.getUserId())) {
                throw new AuthenticationException("Authriencation Fail");
            }
            int resultCount = sqlSession.delete("agent.deleteAgent", agent);
            if(resultCount != 1) {
                throw new OperationException("[FAIL] delete Agent ResultCount: " + resultCount);
            }
            return registerAgent;
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] Delete Agent error");
        }
    }

    private void isNotNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNotNull(Float field, String label) throws ParameterException {
        if(field == null){
            throw new ParameterException(label);
        }
    }
    private void isNotNull(Integer field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }

}