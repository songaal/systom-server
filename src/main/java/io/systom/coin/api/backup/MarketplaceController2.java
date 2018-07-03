//package io.systom.coin.api.backup;
//
//import io.systom.coin.api.AbstractController;
//import io.systom.coin.exception.AbstractException;
//import io.systom.coin.exception.OperationException;
//import io.systom.coin.model.Goods;
//import io.systom.coin.service.backup.MarketplaceService2;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///*
// * create joonwoo 2018. 6. 7.
// *
// */
//@RestController
//@RequestMapping("/v1/marketplace")
//public class MarketplaceController2 extends AbstractController {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketplaceController2.class);
//
//    @Autowired private MarketplaceService2 marketplaceService;
//
////    TODO 판매등록, 판매삭제,
////    TODO 판매목록 -> 심볼별 (* is_display = true)
////    TODO 판매 전략 판매상세 정보(거래소, 심볼 버전 등등)
//
//
//    @PostMapping
//    public ResponseEntity<?> releases(@RequestAttribute String userId,
//                                      @RequestBody Goods goods) {
//        try {
//            Goods registerGoods = marketplaceService.releases(userId, goods);
//            return success(registerGoods);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException().response();
//        }
//    }
//
//
//}
