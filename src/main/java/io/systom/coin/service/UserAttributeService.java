package io.systom.coin.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.BillingException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Card;
import io.systom.coin.model.UserAttribute;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserAttributeService {

    private static Logger logger = LoggerFactory.getLogger(UserAttributeService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private BillingService billingService;
    @Autowired
    private CardService cardService;

    @Value("${membership.price}")
    private String memberShipPrice;

    public UserAttribute getPaidPlan(String userId) {
        if (userId == null || "".equalsIgnoreCase(userId)) {
            return null;
        }
        UserAttribute userAttribute;
        try {
            userAttribute = sqlSession.selectOne("userAttribue.getPaidPlan", userId);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
        return userAttribute;
    }

    @Transactional
    public void insertPaidPlan(UserAttribute userAttribute) {

        Card defaultCard = sqlSession.selectOne("getUserDefaultCard", userAttribute.getUserId());
        if (defaultCard == null) {
            throw new OperationException("[FAIL] NotFound Default Card");
        }

        try {
            userAttribute.setPaidUser(true);
            userAttribute.setCanceled(false);
            userAttribute.setCustomerUid(defaultCard.getCustomerUid());

            int changeRow = sqlSession.insert("userAttribue.insertPaidPlan", userAttribute);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

//      결재 진행.
        String customerUid = defaultCard.getCustomerUid();
        String merchantUid = "systom-" + System.nanoTime();
        try {
            // 즉시결재
            String description = String.format("%s월 멤버십비용", new SimpleDateFormat("M").format(new Date()));
            Map result = billingService.makePayment(customerUid, merchantUid, memberShipPrice, description);
            logger.debug("makePayment >> {}", result);
        } catch (BillingException e) {
            logger.error("", e);
            throw new OperationException(e.getMessage());
        }

        try {
            // 1달후 예약.
            Calendar nextDateTime = Calendar.getInstance();
            nextDateTime.add(Calendar.MONTH, 1);
            merchantUid = "systom-" + System.nanoTime();
            String description = String.format("%s월 멤버십비용", new SimpleDateFormat("M").format(nextDateTime.getTime()));
            billingService.schedulePayment(customerUid, merchantUid, memberShipPrice, description, nextDateTime);
        } catch (BillingException e) {
            logger.error("", e);
            throw new OperationException(e.getMessage());
        }

    }

    @Transactional
    public void updateCancelPlan(UserAttribute userAttribute) {
        UserAttribute registerUserAttr = getPaidPlan(userAttribute.getUserId());
        if (registerUserAttr == null) {
            throw new ParameterException();
        }
        String customerUid = registerUserAttr.getCustomerUid();

        try {
            int changeRow = sqlSession.update("userAttribue.updatePaidPlan", userAttribute);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        if (!userAttribute.isPaidUser()) {
//            예약 취소
            try {
                Map result = billingService.cancelScheduledPayment(customerUid);
                logger.debug("cancelScheduledPayment >> {}", result);
                logger.debug("카드 {}의 모든 스케줄이 취소되었습니다.", customerUid);
            } catch (BillingException e) {
                if (!e.getMessage().equalsIgnoreCase("취소할 예약결제 기록이 존재하지 않습니다.")) {
                    logger.error("", e);
                    throw new OperationException(e.getMessage());
                } else {
                    logger.debug("customerUid:{}, error: {}", customerUid, e.getMessage());
                }
            }
        }

        if (userAttribute.isPaidUser()) {
//            재가입인 경우
            Card defaultCard = sqlSession.selectOne("getUserDefaultCard", userAttribute.getUserId());
            customerUid = defaultCard.getCustomerUid();
            registerUserAttr.setCustomerUid(customerUid);
            try {
                int changeRow = sqlSession.update("userAttribue.updateCustomerUid", registerUserAttr);
                if (changeRow != 1) {
                    throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
                }
            } catch (Exception e){
                logger.error("", e);
                throw new OperationException("[FAIL] SQL Execute.");
            }

//            다시 진행
            try {
                // 1달후 예약.
                Calendar nextDateTime = Calendar.getInstance();
                nextDateTime.add(Calendar.MONTH, 1);
                nextDateTime.set(Calendar.DATE, registerUserAttr.getPaymentDay());
                String merchantUid = "systom-" + System.nanoTime();
                String description = String.format("%s월 멤버십비용", new SimpleDateFormat("M").format(nextDateTime.getTime()));
                billingService.schedulePayment(customerUid, merchantUid, memberShipPrice, description, nextDateTime);
            } catch (BillingException e) {
                logger.error("", e);
                throw new OperationException(e.getMessage());
            }
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
