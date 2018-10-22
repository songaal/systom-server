package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.exception.BillingException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Card;
import io.systom.coin.model.MembershipInvoice;
import io.systom.coin.model.UserAttribute;
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
    private static Logger paymentLogger = LoggerFactory.getLogger("paymentLogger");
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private BillingService billingService;
    @Autowired
    private CardService cardService;
    @Autowired
    private InvoiceService invoiceService;
    @Value("${membership.price}")
    private String memberShipPrice;
    @Value("${membership.termUnit}")
    private String memberShipTermUnit;
    @Value("${membership.term}")
    private Integer memberShipTerm;

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
        String description = String.format("%s월 멤버십비용", new SimpleDateFormat("M").format(new Date()));

//        DB ROW 생성
        MembershipInvoice invoice = new MembershipInvoice();
        invoice.setName(description);
        invoice.setPaymentPrice(Float.parseFloat(memberShipPrice));
        invoice.setUserId(userAttribute.getUserId());
        invoice.setCustomerUid(customerUid);
        invoice.setMerchantUid(merchantUid);
        invoice.setWait(true);
        invoice = invoiceService.createMembershipInvoice(invoice);
        paymentLogger.info("인보이스 생성 >> {}", invoice);

        try {
            // 즉시결재
            Map result = billingService.makePayment(customerUid, merchantUid, memberShipPrice, description);
            paymentLogger.info("즉시결제 결과 >> {}", result);
            invoice.setStatus("OK");
            invoice.setPaymentTime(new Date());
            invoice.setPaymentImpUid(String.valueOf(((Map)result.get("response")).get("imp_uid")));
            invoice.setPaymentResult(new Gson().toJson(result));
            invoice.setWait(false);
            invoiceService.updateMembershipInvoice(invoice);
        } catch (BillingException e) {
            logger.error("", e);
            invoice.setStatus("DELAY");
            invoice.setWait(true);
            invoiceService.updateMembershipInvoice(invoice);
            throw new OperationException(e.getMessage());
        } finally {
            addPaymentSchedule(userAttribute.getUserId(), customerUid, null);
        }
    }

    public void addPaymentSchedule (String userId, String customerUid, Calendar nextDateTime) {
        //        예약 DB ROW 생성
        if (nextDateTime == null) {
            nextDateTime = Calendar.getInstance();
            nextDateTime.setTime(new Date());
            if ("MONTH".equalsIgnoreCase(memberShipTermUnit)) {
                nextDateTime.add(Calendar.MONTH, memberShipTerm);
            } else if ("DATE".equalsIgnoreCase(memberShipTermUnit)) {
                nextDateTime.add(Calendar.DATE, memberShipTerm);
            } else if ("MINUTE".equalsIgnoreCase(memberShipTermUnit)) {
                nextDateTime.add(Calendar.MINUTE, memberShipTerm);
            }
        }

        String merchantUid = "systom-" + System.nanoTime();
        String description = String.format("%s월 멤버십비용", new SimpleDateFormat("M").format(nextDateTime.getTime()));

        MembershipInvoice waitInvoice = new MembershipInvoice();
        waitInvoice.setName(description);
        waitInvoice.setPaymentPrice(Float.parseFloat(memberShipPrice));
        waitInvoice.setUserId(userId);
        waitInvoice.setCustomerUid(customerUid);
        waitInvoice.setMerchantUid(merchantUid);
        waitInvoice.setWait(true);
        waitInvoice.setNextPaymentTime(nextDateTime.getTime());
        waitInvoice = invoiceService.createMembershipInvoice(waitInvoice);
        paymentLogger.info("다음달 결제 정보 저장 >> {}", waitInvoice);

        try {
            // 1달후 예약.
            Map result = billingService.schedulePayment(customerUid, merchantUid, memberShipPrice, description, nextDateTime);
            paymentLogger.info("다음회차 스케쥴 등록 >> {}", result);
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
                paymentLogger.info("카드 {}의 모든 스케줄이 취소되었습니다. {}", customerUid, result);
            } catch (BillingException e) {
                if (!e.getMessage().equalsIgnoreCase("취소할 예약결제 기록이 존재하지 않습니다.")) {
                    logger.error("", e);
                    throw new OperationException(e.getMessage());
                } else {
                    logger.debug("customerUid:{}, error: {}", customerUid, e.getMessage());
                }
            }
//            DB도 예약 인보이스 정보 삭제.
            try {
                sqlSession.delete("invoice.deleteWaitMembershipInvoice", registerUserAttr.getUserId());
            } catch (Exception e){
                logger.error("", e);
                throw new OperationException("[FAIL] SQL Execute.");
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
            Calendar nextDateTime = Calendar.getInstance();
            nextDateTime.add(Calendar.MONTH, 1);
            nextDateTime.set(Calendar.DATE, registerUserAttr.getPaymentDay());
            paymentLogger.info("결재 스케쥴러 재신청: {}", nextDateTime);
            addPaymentSchedule(userAttribute.getUserId(), customerUid, nextDateTime);
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
