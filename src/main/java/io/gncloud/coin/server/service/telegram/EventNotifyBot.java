package io.gncloud.coin.server.service.telegram;

import io.gncloud.coin.server.service.IdentityService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/*
 * create joonwoo 2018. 3. 24.
 * 
 */
public class EventNotifyBot extends TelegramLongPollingBot {

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(EventNotifyBot.class);

    @Value("${notify.telegram.use}")
    private boolean use;

    private EventWatchTelegramService service;
    private IdentityService identityService;

    private String username;
    private String token;

    public EventNotifyBot(EventWatchTelegramService service, IdentityService identityService, String username, String token){
        this.service = service;
        this.identityService = identityService;
        this.username = username;
        this.token = token;
    }

    public void sendMessage(long chatId, String text) {
        if(!use){
            return;
        }
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("SendMessage error chatId[%s] text[%s]", chatId, text), e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {

                logger.debug("Receive >> {}", update.getMessage());
                long chatId = update.getMessage().getChatId();
                String text = update.getMessage().getText();
                String userId = service.getUserByTelegram(chatId);

                String responseText = null;

                if (userId == null) {
                    responseText = String.format("코인클라우드 계정설정에서 텔레그램 계정을 연동해주세요. 당신의 텔레그램 아이디는 %s 입니다.", chatId);
                } else {
                    if (text.equals("/quit")) {
                        service.unsetByChatId(chatId);
                        responseText = "계정연결을 성공적으로 취소했습니다.";
                    } else {
                        responseText = "알 수 없는 명령입니다.";
                    }
                }

                sendMessage(chatId, responseText);
            }
        } catch (Throwable t) {
            logger.error("Telegram read message error.", t);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }




}