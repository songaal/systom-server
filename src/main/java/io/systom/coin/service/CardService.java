package io.systom.coin.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Card;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardService {

    private static Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private BillingService billingService;

    public int getCardCount(String userId) {
        return sqlSession.selectOne("card.getCardCount", userId);
    }

    public Card registerCard(Card card) {

        isValid(card);
//       TODO 아임포트 카드 등록

//        처음 만들땐 기본카드로 설정
        if (getCardCount(card.getUserId()) == 0) {
            card.setDefault(true);
        }

        try {

//        db 저장은 카드 마지막 4자리
            card.setCardNo(card.getCardNo().substring(12));

            int changeRow = sqlSession.insert("card.registerCard", card);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return card;
    }

    public List<Card> retrieveCardList (String userId) {
        List<Card> cards;
        try {
            cards = sqlSession.selectList("card.retrieveCardList", userId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return cards;
    }

    public Card getCard(Integer id, String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("userId", userId);
        try {
            return sqlSession.selectOne("card.getCard", param);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Card deleteCard(Integer id, String userId) {
        Card searchCard = getCard(id, userId);
        if (searchCard == null) {
            throw new AuthenticationException();
        }

        try {
            int changeRow = sqlSession.delete("card.deleteCard", id);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getCard(id, userId);
    }

    @Transactional
    public Card updateCardDefault(Card card) {
        if (card == null) {
            throw new ParameterException("Card Id");
        }
        Card searchCard = getCard(card.getId(), card.getUserId());
        if (searchCard == null) {
            throw new AuthenticationException();
        }
        try {
//            기존 기본 상태 초기화
            sqlSession.update("card.updateDefaultReset", card);
            int changeRow = sqlSession.update("card.updateDefault", card);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getCard(card.getId(), card.getUserId());
    }

    private void isValid(Card card) throws ParameterException {
        if (StringUtils.isBlank(card.getCardNo()) || StringUtils.isEmpty(card.getCardNo())) {
            throw new ParameterException("Card No");
        }
        if (StringUtils.isBlank(card.getOwner()) || StringUtils.isEmpty(card.getOwner())) {
            throw new ParameterException("Owner");
        }
        if (StringUtils.isBlank(card.getMonth()) || StringUtils.isEmpty(card.getMonth())) {
            throw new ParameterException("Month");
        }
        if (StringUtils.isBlank(card.getYear()) || StringUtils.isEmpty(card.getYear())) {
            throw new ParameterException("Year");
        }
        if (StringUtils.isBlank(card.getBirthDate()) || StringUtils.isEmpty(card.getBirthDate())) {
            throw new ParameterException("BirthDate");
        }
    }

}
