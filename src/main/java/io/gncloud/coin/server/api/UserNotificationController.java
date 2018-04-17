package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.model.UserNotification;
import io.gncloud.coin.server.service.telegram.EventWatchTelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            UserNotification userNotification = telegramService.selectNotification(new UserNotification(userId));
            return success(userNotification);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    @PostMapping("/telegram")
    public ResponseEntity<?> createTelegramNotification(@RequestAttribute String userId, @RequestBody UserNotification userNotification) {
        try {
            telegramService.insertNotification(userNotification);
            telegramService.sendMessage(userId, String.format("코인클라우드 %s 계정과 성공적으로 연결되었습니다. ChatId=[%d]", userId, userNotification.getServiceUser()));
            return success(userNotification);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    @DeleteMapping("/telegram")
    public ResponseEntity<?> deleteTelegramNotification(@RequestAttribute String userId, @RequestBody UserNotification userNotification) {
        try {
            telegramService.deleteNotification(userNotification);
            return success(userNotification);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

}
