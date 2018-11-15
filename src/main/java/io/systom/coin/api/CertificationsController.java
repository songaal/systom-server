package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Certification;
import io.systom.coin.service.CertificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/certifications")
public class CertificationsController extends AbstractController {
    private static Logger logger = LoggerFactory.getLogger(CertificationsController.class);
    
    @Autowired
    private CertificationService certificationService;

    @GetMapping
    public ResponseEntity<?> getCertification(@RequestAttribute String userId) {
        try {
            Certification certification = certificationService.getCertification(userId);
            return success(certification);
        } catch(AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable e) {
            logger.error("", e);
            return new OperationException().response();
        }
    }


    @PostMapping
    public ResponseEntity<?> insertCertification(@RequestAttribute String userId, @RequestBody Certification certification) {
        try {
//            TODO 다날 API 조회.

            certification.setUserId(userId);
            certificationService.insertCertification(certification);

            return success(certification);
        } catch(AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable e) {
            logger.error("", e);
            return new OperationException().response();
        }
    }


}
