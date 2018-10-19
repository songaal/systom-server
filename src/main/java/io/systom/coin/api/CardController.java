package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Card;
import io.systom.coin.service.BillingService;
import io.systom.coin.service.CardService;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cards")
public class CardController extends AbstractController{
    private static Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardService cardService;

    @Autowired
    private BillingService billingService;

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

    /**
     * 아임포트 webhook은 다음과 같은 경우에 호출됩니다.
     * - 결제가 승인되었을 때(모든 결제 수단)
     * - 예약결제가 시도되었을 때
     * - 대시보드에서 환불되었을 때
     *
     * Webhook이 호출되면 설정한 콜백 url에 대해 다음과 같은 POST요청을 생성합니다.
     * @param payload
     * @return
     */
    @PostMapping("/iamport-callback/schedule")
    public ResponseEntity<?> onIamportWebhook(Map<String, Object> payload) {
        try {
            String impUid = (String) payload.get("imp_uid");
            Map paymentInfo = billingService.getPayment(impUid);
            /*
            * << PaymentInfo 예시 >>
            * {code=0, message=null,
            *   response={
            *      amount=100, apply_num=59102455, bank_code=null, bank_name=null, buyer_addr=null, buyer_email=null,
            *      buyer_name=null, buyer_postcode=null, buyer_tel=null,
            *      cancel_amount=0, cancel_history=[], cancel_reason=null, cancel_receipt_urls=[], cancelled_at=0,
            *      card_code=361, card_name=BC카드, card_quota=0, cash_receipt_issued=false, channel=api, currency=KRW,
            *      custom_data=null, escrow=false, fail_reason=null, failed_at=0,
            *      imp_uid=imps_370951057727,
            *      merchant_uid=systom-12850260841578,
            *      name=유료플랜 1000,
            *      paid_at=1539919951,
            *      pay_method=card, pg_id=nictest04m, pg_provider=nice, pg_tid=nictest04m01161810191232297140,
            *      receipt_url=https://pg.nicepay.co.kr/issue/IssueLoader.jsp?TID=nictest04m01161810191232297140&type=0,
            *      status=paid, user_agent=sorry_not_supported_anymore, vbank_code=null, vbank_date=0, vbank_holder=null,
            *      vbank_name=null, vbank_num=null
            *   }
            * }
            * */
            int code = (int) paymentInfo.get("code");
            //호출상태 ok 여부
            if (code == 0) {
                Map response = (Map) paymentInfo.get("response");
                //merchant_uid 가 가장중요하다. merchant_uid 로 db를 조회해서 결제완료로 업데이트 해준다.
                String merchant_uid = (String) response.get("merchant_uid");
                String status = (String) response.get("status");
                String amount = (String) response.get("amount"); //금액
                String currency = (String) response.get("currency"); // 통화. 대부분 KRW
                String fail_reason = (String) response.get("fail_reason");
                String cancel_reason = (String) response.get("cancel_reason");
                if (status.equals("paid")) {
                    //TODO  merchant_uid 로 db를 조회해서 결제완료로 업데이트 해준다.



                    ///1달후에 또 걸기.
                } else {

                }
            } else {
                //호출실패.
            }

            return null;
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

}
