package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.User;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * create joonwoo 2018. 3. 24.
 * 
 */
@Service
public class AuthService {

    // TODO AWS cognito 연동

    private Map<String, String> tmpLogin = new ConcurrentHashMap<>();

    public User loginProcess(User user) throws ParameterException {
        isNull(user.getUserId(), "userId");
        isNull(user.getPassword(), "password");

        user.setToken(UUID.randomUUID().toString());
        tmpLogin.put(user.getUserId(), user.getToken());
        return user;
    }

    public User findTokenByUser(String token) throws ParameterException {
        if(!tmpLogin.containsValue(token)){
            throw new ParameterException("token");
        }
        User user = null;
        Iterator<Map.Entry<String, String>> entryIterator = tmpLogin.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<String, String> entry = entryIterator.next();
            if(entry.getValue().equals(token)){
                user = new User();
                user.setUserId(entry.getKey());
                user.setToken(token);
                user.setPassword("*************");
                break;
            }
        }
        return user;
    }

    private void isNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
}