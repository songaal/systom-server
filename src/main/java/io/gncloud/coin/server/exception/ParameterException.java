package io.gncloud.coin.server.exception;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
public class ParameterException extends Exception {

    public ParameterException(String field) {
        super(String.format("There is no %s value.", field));
    }
}