package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.service.InvoiceService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
@RestController
@RequestMapping("/v1/invoice")
public class InvoiceController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<?> retrieveInvoice(@RequestAttribute String userId) {
        try {
            return success(invoiceService.retrieveInvoice(userId));
        } catch (AbstractException e){
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException().response();
        }
    }
}