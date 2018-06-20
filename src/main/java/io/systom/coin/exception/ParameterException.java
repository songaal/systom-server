package io.systom.coin.exception;

import org.springframework.http.HttpStatus;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
public class ParameterException extends AbstractException {

    public ParameterException() {
        super(HttpStatus.BAD_REQUEST, "There is Parameter Field Value");
    }

    public ParameterException(String field) {
        super(HttpStatus.BAD_REQUEST, String.format("There is no %s value.", field));
    }
}