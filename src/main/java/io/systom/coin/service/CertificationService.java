package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.SqlException;
import io.systom.coin.model.Certification;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificationService {
    private static Logger logger = LoggerFactory.getLogger(CertificationService.class);

    @Autowired
    private SqlSession sqlSession;


    public Certification getCertification(String userId) {
        return sqlSession.selectOne("userAttribute.getCertification", userId);
    }

    public void insertCertification(Certification certification) {
        try {
            int changeRow = sqlSession.insert("userAttribute.insertCertification", certification);
//            if (changeRow != 1) {
//                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
//            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }
}
