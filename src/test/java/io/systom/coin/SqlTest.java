//package io.systom.coin;
//
//import io.systom.coin.model.Goods;
//import io.systom.coin.model.InvestGoods;
//import io.systom.coin.model.TraderTaskResult;
//import org.apache.ibatis.session.SqlSession;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///*
// * create joonwoo 2018. 7. 4.
// *
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = Application.class)
//public class SqlTest {
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    @Test
//    public void selectTest(){
//        Goods searchGoods = new Goods();
//        searchGoods = new Goods();
//        searchGoods.setAuthorId("0");
//        searchGoods.setExchange("binance");
////        searchGoods.setCollectStart(System.currentTimeMillis());
////        searchGoods.setCollectEnd(System.currentTimeMillis());
//        List<Goods> registerGoodsList = sqlSession.selectList("goods.retrieveGoodsList", searchGoods);
//
//        List<Integer> goodsIdList = new ArrayList<>();
//        registerGoodsList.forEach(goods -> {
//            goodsIdList.add(goods.getId());
//        });
//        int registerGoodsSize = registerGoodsList.size();
//        if (registerGoodsSize > 0) {
//            Map<String, Object> search = new HashMap<>();
//            search.put("goodsIdList", goodsIdList);
//            search.put("userId", searchGoods.getAuthorId());
//            List<InvestGoods> investGoodsList = sqlSession.selectList("goods.retrieveInvestGoodsList", search);
//            int investGoodsSize = investGoodsList.size();
//
//            List<TraderTaskResult.Result> performanceList = sqlSession.selectList("goods.retrievePerformance", search);
//            int performanceSize = performanceList.size();
//
//            for (int i=0; i < investGoodsSize; i++) {
//                Integer goodsId = investGoodsList.get(i).getGoodsId();
//                Integer investId = investGoodsList.get(i).getId();
//
//                Goods tmpGoods = null;
//                TraderTaskResult.Result tmpPerformance = null;
//
//                for(int j=0; j < registerGoodsSize; j++){
//                    if(goodsId.equals(registerGoodsList.get(j).getId())){
//                        tmpGoods = registerGoodsList.get(j);
//                        break;
//                    }
//                }
//                for(int j=0; j < performanceSize; j++){
//                    if(investId.equals(performanceList.get(j).getId())) {
//                        tmpPerformance = performanceList.get(j);
//                        break;
//                    }
//                }
////                tmpGoods.setPerformance(tmpPerformance);
//            }
//        }
//        System.out.println(registerGoodsList);
//    }
//}