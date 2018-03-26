package io.gncloud.coin.server.exception;

import org.springframework.http.HttpStatus;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
public class OperationException extends AbstractException{

    public OperationException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public OperationException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}