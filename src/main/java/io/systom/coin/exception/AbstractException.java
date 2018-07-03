package io.systom.coin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 24.
 *
 */
public abstract class AbstractException extends RuntimeException {

    protected Map<String, Object> responseBody = new HashMap<>();
    protected HttpStatus status;

    public AbstractException(HttpStatus status) {
        this.status = status;
    }

    public AbstractException(Throwable t) {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        responseBody.put("message", t.getMessage());
    }

    public AbstractException(HttpStatus status, String message) {
        this(status);
        responseBody.put("message", message);
    }

    public ResponseEntity<?> response() {
        return new ResponseEntity<>(responseBody, status);
    }
}