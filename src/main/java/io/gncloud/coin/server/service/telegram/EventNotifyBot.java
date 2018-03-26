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

    private String username;
    private String token;

    public EventNotifyBot(String username, String token){
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