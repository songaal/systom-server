package io.systom.coin;

import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.model.Task;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.InvestGoodsService;
import io.systom.coin.service.TaskService;
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
            Task task = new Task();
            task.setUserId("joonwoo");
            task.setGoodsId(investGoods.getId());
            task.setStrategyId(investGoods.getStrategyId());
            task.setExchange(investGoods.getExchange());
            task.setCoinUnit(investGoods.getCoinUnit());
            task.setBaseUnit(investGoods.getBaseUnit());
            task.setCashUnit(investGoods.getCashUnit());
            task.setStartDate(investGoods.getCollectStart());
            task.setEndDate(investGoods.getCollectEnd());
            investGoods = taskService.createGoodsBackTest(task);

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
            Task task = new Task();
            task.setUserId("joonwoo");
            task.setGoodsId(investGoods.getId());
            task.setStrategyId(investGoods.getStrategyId());
            task.setExchange(investGoods.getExchange());
            task.setCoinUnit(investGoods.getCoinUnit());
            task.setBaseUnit(investGoods.getBaseUnit());
            task.setCashUnit(investGoods.getCashUnit());
            task.setStartDate(investGoods.getCollectStart());
            task.setEndDate(investGoods.getCollectEnd());
            investGoods = taskService.createGoodsBackTest(task);

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
        logger.info("상품 투자 등록[testuser] {}" , investGoodsService.registrationInvestor(investor));

        logger.info("상품 정보 조회 {}" , goodsService.getGoods(investor.getGoodsId()));

        logger.info("상품 투자 취소[testuser] {}" , investGoodsService.removeInvestor(investor.getId(), "testuser"));

        logger.info("상품 정보 조회 {}" , goodsService.getGoods(investor.getGoodsId()));


        logger.info("상품 정보 삭제[{}]{}" , investGoods.getId(), goodsService.deleteGoods(investGoods.getId(), "joonwoo"));

    }

}