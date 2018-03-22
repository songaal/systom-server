package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Algo;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@Service
public class AlgosService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AlgosService.class);

    @Autowired
    private SqlSession sqlSession;

    public Algo insertAlgo(String userId, String source) throws OperationException, ParameterException {

        if(userId == null || "".equals(userId)){
            throw new ParameterException("userId");
        }

        if(source == null || "".equals(source)){
            throw new ParameterException("source");
        }

        Algo algo = new Algo();
        algo.setAlgoId(UUID.randomUUID().toString());
        algo.setCreateTime(new Date());
        algo.setUserId(userId);
        algo.setSource(source);

        logger.debug("INSERT ALGO: {}", algo);

        int result = sqlSession.insert("algos.insertAlgo", algo);
        if(result != 1){
            throw new OperationException("[FAIL] Insert Failed Algo Source");
        }
        return getAlgo(algo.getAlgoId());
    }

    public Algo getAlgo(String algoId) throws ParameterException {
        if(algoId == null){
            throw new ParameterException("algoId");
        }
        Algo findAlgo = new Algo();
        findAlgo.setAlgoId(algoId);
        return getAlgo(findAlgo);
    }
    public Algo getAlgo(Algo algo) throws ParameterException {
        if(algo == null){
            throw new ParameterException("algoId");
        }
        return sqlSession.selectOne("algos.getAlgo", algo);
    }

    public List<Algo> findUserIdByAlgo(String userId) throws ParameterException {
        if(userId == null){
            throw new ParameterException("userId");
        }
        Algo findAlgo = new Algo();
        findAlgo.setUserId(userId);
        return sqlSession.selectList("algos.getAlgo", findAlgo);
    }

    public Algo updateAlgo(String algoId, String source) throws ParameterException, OperationException {
        if(algoId == null || "".equals(algoId)){
            throw new ParameterException("algoId");
        }

        if(source == null || "".equals(source)){
            throw new ParameterException("source");
        }

        Algo registerAlgo = getAlgo(algoId);

        registerAlgo.setSource(source);

        logger.debug("UPDATE ALGO: {}", registerAlgo);

        int result = sqlSession.update("algos.updateAlgo", registerAlgo);
        if(result != 1){
            throw new OperationException("[FAIL] Update Failed Algo Source");
        }
        return getAlgo(registerAlgo);
    }

    public void deleteAlgo(String algoId) throws ParameterException, OperationException {

        if(algoId == null || "".equals(algoId)){
            throw new ParameterException("algoId");
        }

        logger.debug("DELETE ALGO: {}", algoId);

        int result = sqlSession.delete("algos.deleteAlgo", algoId);

        if(result != 1){
            throw new OperationException("[FAIL] delete Filed Algo Source");
        }

    }
}