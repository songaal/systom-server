package io.systom.coin.exception;

import org.springframework.http.HttpStatus;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
public class SqlException extends AbstractException {


    public SqlException(HttpStatus status) {
        super(status);
    }

    public SqlException(Throwable t) {
        super(t);
    }

    public SqlException(HttpStatus status, String message) {
        super(status, message);
    }
}