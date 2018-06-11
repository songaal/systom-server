package io.gncloud.coin.server.exception;

import org.springframework.http.HttpStatus;

/*
 * create joonwoo 2018. 6. 11.
 * 
 */
public class RequestException extends AbstractException {

    public RequestException() {
        super(HttpStatus.NOT_MODIFIED);
    }

}