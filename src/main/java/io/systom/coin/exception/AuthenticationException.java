package io.systom.coin.exception;

import org.springframework.http.HttpStatus;

/*
 * create joonwoo 2018. 3. 30.
 * 
 */
public class AuthenticationException extends AbstractException {

    public AuthenticationException() {
        super(HttpStatus.FORBIDDEN, "You do not have permission.");
    }

    public AuthenticationException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}