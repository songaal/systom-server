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

    public Algo insertAlgo(String userId, String code) throws OperationException, ParameterException {

        isNull(userId, "userId");
        isNull(code, "code");

        Algo algo = new Algo();
        algo.setAlgoId(UUID.randomUUID().toString());
        algo.setCreateTime(new Date());
        algo.setUserId(userId);
        algo.setCode(code);

        logger.debug("INSERT ALGO: {}", algo);

        int result = sqlSession.insert("algos.insertAlgo", algo);
        if(result != 1){
            throw new OperationException("[FAIL] Insert Failed Algo Source");
        }
        return getAlgo(algo.getAlgoId());
    }

    public Algo getAlgo(String algoId) throws ParameterException {
        isNull(algoId, "algoId");

        Algo findAlgo = new Algo();
        findAlgo.setAlgoId(algoId);
        return getAlgo(findAlgo);
    }
    public Algo getAlgo(Algo algo) throws ParameterException {
        isNull(algo, "algo");

        return sqlSession.selectOne("algos.getAlgo", algo);
    }

    public List<Algo> findUserIdByAlgo(String userId) throws ParameterException {
        isNull(userId, "userId");

        Algo findAlgo = new Algo();
        findAlgo.setUserId(userId);
        List<Algo> algoList = sqlSession.selectList("algos.getAlgo", findAlgo);
        return algoList;
    }

    public Algo updateAlgo(String algoId, String code) throws ParameterException, OperationException {
        isNull(algoId, "algoId");
        isNull(code, "code");

        Algo registerAlgo = getAlgo(algoId);
        registerAlgo.setCode(code);

        logger.debug("UPDATE ALGO: {}", registerAlgo);

        int result = sqlSession.update("algos.updateAlgo", registerAlgo);
        if(result != 1){
            throw new OperationException("[FAIL] Update Failed Algo Source");
        }
        return getAlgo(registerAlgo);
    }

    public void deleteAlgo(String algoId) throws ParameterException, OperationException {
        isNull(algoId, "algoId");

        logger.debug("DELETE ALGO: {}", algoId);
        int result = sqlSession.delete("algos.deleteAlgo", algoId);
        if(result != 1){
            throw new OperationException("[FAIL] delete Filed Algo Source");
        }
    }

    private void isNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNull(Algo algo, String label) throws ParameterException {
        if(algo == null){
            throw new ParameterException(label);
        }
    }
}