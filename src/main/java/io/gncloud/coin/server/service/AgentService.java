package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.Agent;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * create joonwoo 2018. 4. 13.
 * 
 */
@Service
public class AgentService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AgentService.class);
    @Autowired
    private SqlSession sqlSession;


    public Agent insertAgent(Agent agent) throws OperationException {
        try {
            int resultCount = sqlSession.insert("agent.insertAgent", agent);
            if (resultCount != 1) {
                throw new OperationException("[FAIL] Agent Insert resultCount: " + resultCount);
            }
            return agent;
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] Agent Insert error");
        }
    }
}