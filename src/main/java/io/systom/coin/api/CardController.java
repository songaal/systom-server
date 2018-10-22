package io.systom.coin.api;

import com.google.gson.Gson;
import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.BillingException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Card;
import io.systom.coin.model.MembershipInvoice;
import io.systom.coin.model.UserAttribute;
import io.systom.coin.service.BillingService;
import io.systom.coin.service.CardService;
import io.systom.coin.service.InvoiceService;
import io.systom.coin.service.UserAttributeService;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cards")
public class CardController extends AbstractController{
    private static Logger logger = LoggerFactory.getLogger(CardController.class);
    private static Logger paymentLogger = LoggerFactory.getLogger("paymentLogger");

    @Autowired
    private CardService cardService;

    @Autowired
    private BillingService billingService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserAttributeService userAttributeService;

    @Value("${membership.termUnit}")
    private String memberShipTermUnit;
    @Value("${membership.term}")
    private Integer memberShipTerm;

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
    public ResponseEntity<?> onIamportWebhook(@RequestBody Map<String, Object> payload) {
        paymentLogger.info("API 콜백 응답받음. >> {}", payload);
        try {
            String impUid = (String) payload.get("imp_uid");
            Map paymentInfo = billingService.getPayment(impUid);

            int code = (int) paymentInfo.get("code");
            //호출상태 ok 여부
            if (code == 0) {
                Map response = (Map) paymentInfo.get("response");
                //merchant_uid 가 가장중요하다. merchant_uid 로 db를 조회해서 결제완료로 업데이트 해준다.
                String merchant_uid = (String) response.get("merchant_uid");
                String status = (String) response.get("status");
                MembershipInvoice invoice;
                synchronized (this) {
                    invoice = invoiceService.getWaitMembershipInvoice(merchant_uid);
                    if (invoice == null) {
//                    결제 대기 상태가 아니면 진행 안함.
                        return success();
                    }
                }
                invoice.setPaymentTime(new Date());
                invoice.setPaymentImpUid(impUid);
                invoice.setWait(false);
                invoice.setPaymentImpUid(impUid);

                if (status.equals("paid")) {
                    invoice.setStatus("OK");
                    invoice.setPaymentResult(new Gson().toJson(response));
                    invoiceService.updateMembershipInvoice(invoice);
                } else {
                    invoice.setStatus("DELAY");
                    invoice.setWait(false);
                    invoiceService.updateMembershipInvoice(invoice);
                }

//              1달후에 또 걸기.
                UserAttribute userAttribute = userAttributeService.getPaidPlan(invoice.getUserId());
                Calendar nextDateTime = Calendar.getInstance();
                nextDateTime.setTime(new Date());
                if ("MONTH".equalsIgnoreCase(memberShipTermUnit)) {
                    nextDateTime.add(Calendar.MONTH, memberShipTerm);
                } else if ("DATE".equalsIgnoreCase(memberShipTermUnit)) {
                    nextDateTime.add(Calendar.DATE, memberShipTerm);
                } else if ("MINUTE".equalsIgnoreCase(memberShipTermUnit)) {
                    nextDateTime.add(Calendar.MINUTE, memberShipTerm);
                }
                nextDateTime.set(Calendar.DATE, userAttribute.getPaymentDay());

                paymentLogger.info("결재 스케쥴러 재신청: {}", nextDateTime.getTime());
                userAttributeService.addPaymentSchedule(userAttribute.getUserId(), invoice.getCustomerUid(), nextDateTime);
            } else {
                //호출실패.
                paymentLogger.error("============== 결제 실패 ================");
                paymentLogger.error("[ 결제 내용 ]", payload);
                paymentLogger.error("============== 결제 실패 ================");
            }

            return success();
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

}
