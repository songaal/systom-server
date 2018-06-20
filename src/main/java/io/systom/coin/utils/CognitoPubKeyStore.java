package io.systom.coin.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * https://cognito-idp.{region}.amazonaws.com/{userPoolId}/.well-known/jwks.json
 */
public class CognitoPubKeyStore {

    private static Logger logger = LoggerFactory.getLogger(CognitoPubKeyStore.class);

    private String url;

    private Map<String, Map> keyMap;

    public CognitoPubKeyStore(String userPoolId) {
        String region = userPoolId.split("_")[0];
        url = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", region, userPoolId);
        updateKeys();
    }

    public synchronized void updateKeys() {
        Map<String, Map> newKeyMap = new HashMap();
        Map<String, Object> map = RestUtils.getObject(url, Map.class);
        List<Map> list = (List) map.get("keys");
        if(list != null) {
            for(Map<String, String> km : list) {
                newKeyMap.put(km.get("kid"), km);
            }
        }
        keyMap = newKeyMap;
    }

    //단순존재확인.
    public boolean containsKey(String kid) {

        if(!keyMap.containsKey(kid)) {
            updateKeys();
            if(!keyMap.containsKey(kid)) {
                return false;
            }
        }
        return true;
    }


    // 존재하고 유효기간 지나지 않았는지 확인. api 용도.
    public boolean isValid(String kid) {
        if(!containsKey(kid)) {
            return false;
        }

        Map<String, String> obj = keyMap.get(kid);
        String expStr = obj.get("exp");
        long timestamp = Long.parseLong(expStr);

        //TODO  api 사용중이라면 REFRESH 토큰으로 연장한다.


        return new Date(timestamp).after(new Date());
    }


}
