package io.systom.coin.exception;

import org.springframework.http.HttpStatus;

/*
 * create joonwoo 2018. 6. 11.
 * 
 */
public class RequestException extends AbstractException {

    public RequestException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public RequestException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }

}