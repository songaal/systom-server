package io.gncloud.coin.server.service.telegram;

import org.slf4j.LoggerFactory;
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

    private EventWatchTelegramService service;
    private String username;
    private String token;

    public EventNotifyBot(EventWatchTelegramService service, String username, String token){
        this.service = service;
        this.username = username;
        this.token = token;
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
// We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                logger.error("", e);
            }

            //TODO 추가해달라고 하면 인증후 추가해준다.

            String userId = "";
            String chatId = "";
            service.setUserChatId(userId, chatId);
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