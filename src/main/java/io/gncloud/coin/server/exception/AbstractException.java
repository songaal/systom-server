package io.gncloud.coin.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 24.
 *
 */
public abstract class AbstractException extends Throwable {

    protected Map<String, Object> responseBody = new HashMap<>();
    protected HttpStatus status;

    protected AbstractException(HttpStatus status) {
        this.status = status;
    }

    protected AbstractException(HttpStatus status, String message) {
        this(status);
        responseBody.put("message", message);
    }

    public ResponseEntity<?> response() {
        return new ResponseEntity<>(responseBody, status);
    }
}