package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.model.Invoice;
import io.systom.coin.model.PerformanceSummary;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
@Service
public class InvoiceService {
    private static Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private PerformanceService performanceService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private InvitationService invitationService;

    @Value("${invest.maxFriendsSaleCount}")
    private int maxFriendsSaleCount;
    @Value("${invest.initCommission}")
    private float initCommission;

    public List<Invoice> retrieveInvoice(String userId) {
        return sqlSession.selectList("invoice.retrieveInvoice", userId);
    }

    public void createInvoice(InvestGoods investor) {
//        모의투자는 과금발생안함.
        if (investor.isPaper()) {
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setInvestId(investor.getId());
        invoice.setInitCash(investor.getInvestCash());

        PerformanceSummary perf = performanceService.getPerformanceSummary(investor.getId());
        if (perf.getInitCash() < perf.getEquity()) {
            invoice.setReturns(perf.getReturns());
            invoice.setStatus(null);
            int friendCount = invitationService.getFriendsCount(investor.getUserId());
            float discount  = friendCount > maxFriendsSaleCount ? 5 : friendCount;
            float commissionRate = ((initCommission - discount) / 100);
            float paymentPrice = (perf.getEquity() - perf.getInitCash()) * commissionRate;
            invoice.setCommissionRate(commissionRate);
            invoice.setPaymentPrice(paymentPrice);
        } else {
//            수익이 없음..
            invoice.setReturns(0f);
            invoice.setPaymentPrice(0f);
            invoice.setCommissionRate(0f);
            invoice.setStatus("OK"); // 수수료 없을경우 OK
        }

        try {
            int changeRow = sqlSession.insert("invoice.createInvoice", invoice);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

}