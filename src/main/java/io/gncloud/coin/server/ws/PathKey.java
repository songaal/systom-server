package io.gncloud.coin.server.ws;


import io.gncloud.coin.server.model.WsMessage;

public class PathKey {


    public static String getKey(String market, String base, String coin, String interval) {
        String key = new String();
        if(market != null && base != null && coin != null && interval != null){
            key = ( "/" + market + "/" + base+ "/" + coin + "/" + interval );
        }
        if(interval == null){
            key = ( "/" + market + "/" + base+ "/" + coin + "/*" );
        }
        if(coin == null){
            key = ( "/" + market + "/" + base + "/*" );
        }
        if(base == null){
            key = ( "/" + market + "/*" );
        }
        if(market == null){
            key = ( "/*" );
        }
        return key.toLowerCase();
    }

    public static String getKey(WsMessage wsMessage){
        return getKey(wsMessage.getMarket(), wsMessage.getBase(), wsMessage.getCoin(), wsMessage.getInterval());
    }
}
