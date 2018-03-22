package io.gncloud.coin.server.config;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
public class Env {

    public static String getCoinHome(){
        return System.getenv("COIN_HOME");
    }

    public static boolean isDev(){
        return "true".equalsIgnoreCase(System.getenv("COIN_DEV"));
    }

}