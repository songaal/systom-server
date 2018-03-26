package io.gncloud.coin.server.service.telegram;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

/*
 * create joonwoo 2018. 3. 24.
 * 
 */

public class EventWatchTelegramService implements EnvironmentAware {

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(EventWatchTelegramService.class);

    private EventNotifyBot bot;

    @Autowired
    private Environment env;

    @PostConstruct
    public void init(){
        logger.info("Construct TelegramBot.");
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            bot = new EventNotifyBot("username", "token");
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.error("", e);
        }
    }

    public void message(String chatId, String text){
        bot.sendMessage(chatId, text);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}