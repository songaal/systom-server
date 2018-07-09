package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.service.InvestGoodsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * create joonwoo 2018. 7. 6.
 * 
 */
@RestController
@RequestMapping("/v1/investGoods")
public class InvestGoodsController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private InvestGoodsService investGoodsService;

    @GetMapping("/{goodsId}")
    public ResponseEntity<?> getInvestGoods(@RequestAttribute String userId,
                                         @PathVariable Integer goodsId) {

        try {
            InvestGoods registerInvestGoods = investGoodsService.findInvestIdByUser(goodsId, userId);
            if (registerInvestGoods == null) {
                throw new ParameterException("goodsId");
            }
            return success(registerInvestGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @PostMapping("/{goodsId}")
    public ResponseEntity<?> registrationInvestor(@PathVariable Integer goodsId,
                                                  @RequestAttribute String userId,
                                                  @RequestBody InvestGoods investor) {
        try {
            investor.setGoodsId(goodsId);
            investor.setUserId(userId);
            InvestGoods investGoods = investGoodsService.registrationInvestor(investor);
            return success(investGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @DeleteMapping("/{goodsId}")
    public ResponseEntity<?> removeInvestor(@PathVariable Integer goodsId,
                                            @RequestAttribute String userId,
                                            @RequestBody InvestGoods investor) {
        try {
            investor.setGoodsId(goodsId);
            investor.setUserId(userId);
            InvestGoods investGoods = investGoodsService.removeInvestor(investor);
            return success(investGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }


}