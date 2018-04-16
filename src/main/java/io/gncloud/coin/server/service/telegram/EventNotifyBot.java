package io.gncloud.coin.server.service.telegram;

import io.gncloud.coin.server.model.User;
import io.gncloud.coin.server.service.IdentityService;
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
        if (update.hasMessage() && update.getMessage().hasText()) {

            logger.debug("Receive >> {}", update.getMessage());
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            String userId = service.getUserByTelegram(chatId);

            String responseText = null;

            if(userId == null) {
                //인증요청
                if(text.startsWith("/add")) {
                    String[] cmd = text.split("\\s");
                    if(cmd.length != 3) {
                        responseText = "요청이 잘못되었습니다.";
                    } else {
                        if(!cmd[0].equals("/add")) {
                            responseText = "Command 가 잘못되었습니다. /add 로 추정됩니다.";
                        } else {
                            String serviceId = cmd[1];
                            String servicePassword = cmd[2];
                            try {
//                                identityService.login(serviceId, servicePassword);
                                //추가.
                                responseText = "인증성공. 이제 메시지를 받을 수 있습니다. 해제는 언제든 /quit 명령을 입력해주세요.";
                                service.setUserChatId(serviceId, chatId);
                            } catch (Throwable t) {
                                responseText = "인증이 실패했습니다. 아이디와 비밀번호를 확인해주세요.";
                            }
                        }
                    }
                } else {
                    responseText = "사용에 앞서 인증이 필요합니다.\n사용법: /add [사용자아이디] [암호]";
                }
            } else {
                //환영메시지
                if(text.equals("/quit")) {
                    service.unsetUserChatId(chatId);
                    responseText = "계정연결을 성공적으로 취소했습니다.";
                } else {
                    responseText = "알 수 없는 명령입니다.";
                }
            }

            sendMessage(chatId, responseText);
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