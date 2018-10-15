package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Card;
import io.systom.coin.service.CardService;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cards")
public class CardController extends AbstractController{
    private static Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<?> registerCard(@RequestAttribute String userId,
                                          @RequestBody Card card) {
        try {
            card.setUserId(userId);
            Card registerCard = cardService.registerCard(card);
            return success(registerCard);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveCard(@RequestAttribute String userId) {
        try {
            List<Card> registerCardList = cardService.retrieveCardList(userId);
            return success(registerCardList);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Integer id,
                                        @RequestAttribute String userId) {
        try {
            Card registerCard = cardService.deleteCard(id, userId);
            return success(registerCard);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable Integer id,
                                        @RequestBody Card card,
                                        @RequestAttribute String userId) {
        try {
            card.setUserId(userId);
            card.setId(id);
            Card registerCard = cardService.updateCardDefault(card);
            return success(registerCard);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

}
