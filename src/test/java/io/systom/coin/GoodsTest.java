package io.systom.coin;

import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.model.TraderTask;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.InvestGoodsService;
import io.systom.coin.service.TaskService;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.concurrent.TimeoutException;

/*
 * create joonwoo 2018. 7. 23.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class GoodsTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GoodsTest.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private InvestGoodsService investGoodsService;


    @Test
    public void goodsTest() {
        Goods investGoods = new Goods();
        investGoods.setExchange("binance");
        investGoods.setCoinUnit("eth");
        investGoods.setBaseUnit("btc");
        investGoods.setCashUnit("usdt");
        investGoods.setCash(5000f);
        investGoods.setCollectStart("20180701");
        investGoods.setCollectEnd("20180801");
        investGoods.setInvestStart("20180802");
        investGoods.setInvestEnd("20180903");
        investGoods.setTestStart("20180401");
        investGoods.setTestEnd("20180601");
        investGoods.setAuthorId("joonwoo");
        investGoods.setStrategyId(18);
        investGoods.setVersion(1);
        investGoods.setName("test name");
        investGoods.setDescription("test info");
        logger.info("상품 정보 등록 {}" , goodsService.registerGoods(investGoods));

        Goods filter = new Goods();
        filter.setDisplay(false);
        filter.setCollectStart("20180720");
        filter.setCollectEnd("20180720");
        filter.setAuthorId("testuser");
        filter.setExchange("binance");
        logger.info("상품 목록 조회 {}" , goodsService.retrieveGoodsList(filter));

        logger.info("상품 정보 조회[{}]{}" , investGoods.getId(), goodsService.getGoods(investGoods.getId()));

        logger.info("상품 정보 공개[{}]{}" , investGoods.getId(), goodsService.updateGoodsShow(investGoods.getId(), "joonwoo"));

        try {
            TraderTask traderTask = new TraderTask();
            traderTask.setUserId("joonwoo");
            traderTask.setGoodsId(investGoods.getId());
            traderTask.setStrategyId(investGoods.getStrategyId());
            traderTask.setExchange(investGoods.getExchange());
            traderTask.setCoinUnit(investGoods.getCoinUnit());
            traderTask.setBaseUnit(investGoods.getBaseUnit());
            traderTask.setCashUnit(investGoods.getCashUnit());
            traderTask.setStartDate(investGoods.getCollectStart());
            traderTask.setEndDate(investGoods.getCollectEnd());
            investGoods = taskService.createGoodsBackTest(traderTask);

            logger.info("백테스트 등록[{}]{}" , investGoods.getId(), investGoods);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        logger.info("상품 정보 숨김[{}]{}" , investGoods.getId(), goodsService.updateGoodsHide(investGoods.getId(), "joonwoo"));

        investGoods.setName("update test name");
        investGoods.setCollectStart("20180702");
        investGoods.setDescription("update goods description");

        logger.info("상품 정보 수정[{}]{}" , investGoods.getId(), goodsService.updateGoods(investGoods));

        logger.info("상품 정보 조회[{}]{}" , investGoods.getId(), goodsService.getGoods(investGoods.getId()));

        logger.info("상품 정보 삭제[{}]{}" , investGoods.getId(), goodsService.deleteGoods(investGoods.getId(), "joonwoo"));
    }

    @Test
    public void investTest() {
        Goods investGoods = new Goods();
        investGoods.setExchange("binance");
        investGoods.setCoinUnit("eth");
        investGoods.setBaseUnit("btc");
        investGoods.setCashUnit("usdt");
        investGoods.setCash(5000f);
        investGoods.setCollectStart("20180701");
        investGoods.setCollectEnd("20180801");
        investGoods.setInvestStart("20180802");
        investGoods.setInvestEnd("20180903");
        investGoods.setTestStart("20180401");
        investGoods.setTestEnd("20180601");
        investGoods.setAuthorId("joonwoo");
        investGoods.setStrategyId(18);
        investGoods.setVersion(1);
        investGoods.setName("test name");
        investGoods.setDescription("test info");
        logger.info("상품 정보 등록 {}" , goodsService.registerGoods(investGoods));

        try {
            TraderTask traderTask = new TraderTask();
            traderTask.setUserId("joonwoo");
            traderTask.setGoodsId(investGoods.getId());
            traderTask.setStrategyId(investGoods.getStrategyId());
            traderTask.setExchange(investGoods.getExchange());
            traderTask.setCoinUnit(investGoods.getCoinUnit());
            traderTask.setBaseUnit(investGoods.getBaseUnit());
            traderTask.setCashUnit(investGoods.getCashUnit());
            traderTask.setStartDate(investGoods.getCollectStart());
            traderTask.setEndDate(investGoods.getCollectEnd());
            investGoods = taskService.createGoodsBackTest(traderTask);

            logger.info("백테스트 등록[{}]{}" , investGoods.getId(), investGoods);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        logger.info("상품 정보 공개[{}]{}" , investGoods.getId(), goodsService.updateGoodsShow(investGoods.getId(), "joonwoo"));

        Goods filter = new Goods();
        filter.setDisplay(false);
        filter.setCollectStart("20180720");
        filter.setCollectEnd("20180720");
        filter.setAuthorId("testuser");
        filter.setExchange("binance");
        logger.info("상품 목록 조회 {}" , goodsService.retrieveGoodsList(filter));

        InvestGoods investor = new InvestGoods();
        investor.setUserId("testuser");
        investor.setGoodsId(investGoods.getId());
        investor.setExchangeKeyId(3);
        investor.setInvestCash(500f);
        logger.info("투자 등록[{}] {}" , investor.getUserId(), investGoodsService.registrationInvestor(investor));

        logger.info("상품 정보 조회 {}" , goodsService.getGoods(investor.getGoodsId()));

        logger.info("투자한 상품 정보 조회[{}] {}" , investor.getUserId(), investGoodsService.getInvestGoodsDetail(investor.getId(), investor.getUserId()));

//        MonthCalculationScheduler monthCalculationScheduler = new MonthCalculationScheduler();
//        monthCalculationScheduler.setSqlSession(sqlSession);
//        monthCalculationScheduler.monthCalculation();

//        logger.info("투자 취소[{}] {}" , investor.getUserId(), investGoodsService.removeInvestor(investor.getId(), investor.getUserId()));

//        monthCalculationScheduler.monthCalculation();

        logger.info("상품 정보 조회 {}" , goodsService.getGoods(investor.getGoodsId()));

//        logger.info("상품 정보 삭제[{}]{}" , investGoods.getAuthorId(), goodsService.deleteGoods(investGoods.getId(), investGoods.getAuthorId()));

    }

}