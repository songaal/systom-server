package io.systom.coin.config;

/*
 * create joonwoo 2018. 7. 27.
 * 
 */
public class Env {

    public static boolean isLiveExecution() {
        return "true".equalsIgnoreCase(System.getenv("isLiveExecution"));
    }
}