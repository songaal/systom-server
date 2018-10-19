package io.systom.coin.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Card;
import io.systom.coin.model.UserAttribute;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAttributeService {

    private static Logger logger = LoggerFactory.getLogger(UserAttributeService.class);

    @Autowired
    private SqlSession sqlSession;

    public UserAttribute getPaidPlan(String userId) {
        if (userId == null || "".equalsIgnoreCase(userId)) {
            return null;
        }
        return sqlSession.selectOne("userAttribue.getPaidPlan", userId);
    }

    public void insertPaidPlan(UserAttribute userAttribute) {
        try {
            userAttribute.setPaidUser(true);
            userAttribute.setCanceled(false);
            int changeRow = sqlSession.insert("userAttribue.insertPaidPlan", userAttribute);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public void updateCancelPlan(UserAttribute userAttribute) {
        try {
            int changeRow = sqlSession.update("userAttribue.updatePaidPlan", userAttribute);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public boolean isPaidPlan(String userId) {
        UserAttribute userAttribute = getPaidPlan(userId);
//        가입내역 없으면 false
        if (userAttribute == null) {
            return false;
        }
        Date nowDate = new Date();
        if(userAttribute.isPaidUser() == true) {
//        가입 중
            return true;
        }else if (userAttribute.isCanceled() == true && nowDate.getTime() <= userAttribute.getDueDate().getTime()) {
//        취소 중
            return true;
        }

        return false;
    }

}
