package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.model.UserNotification;
import io.gncloud.coin.server.service.EventWatchTelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/notification", produces = "application/json")
public class UserNotificationController extends AbstractController {

    private static Logger logger = LoggerFactory.getLogger(UserNotificationController.class);

    @Autowired
    private EventWatchTelegramService telegramService;

    @GetMapping("/telegram")
    public ResponseEntity<?> getTelegramNotification(@RequestAttribute String userId) {
        try {
            UserNotification userNotification = telegramService.selectNotification(new UserNotification(userId)
                    .withUserId(userId)
                    .withServiceName(EventWatchTelegramService.TELEGRAM_SERVICE));
            return success(userNotification);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    @PostMapping("/telegram")
    public ResponseEntity<?> createTelegramNotification(@RequestAttribute String userId, @RequestBody UserNotification userNotification) {
        try {
            userNotification.setUserId(userId);
            userNotification.setServiceName(EventWatchTelegramService.TELEGRAM_SERVICE);
            telegramService.setUserChatId(userNotification);
            telegramService.sendMessage(userId, String.format("코인클라우드 %s 계정과 성공적으로 연결되었습니다.", userId));
            return success(userNotification);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    @DeleteMapping("/telegram")
    public ResponseEntity<?> deleteTelegramNotification(@RequestAttribute String userId, @RequestBody UserNotification userNotification) {
        try {
            UserNotification request = userNotification.withUserId(userId).withServiceName(EventWatchTelegramService.TELEGRAM_SERVICE);
            UserNotification notification = telegramService.selectNotification(request);
            if(notification != null) {
                telegramService.deleteNotification(notification);
                return success(notification);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

}
