package io.gncloud.coin.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/*
 * create joonwoo 2018. 3. 24.
 *
 */
public abstract class AbstractController {

    protected ResponseEntity<?> success() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    protected ResponseEntity<?> success(Object body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    protected ResponseEntity<?> success(Object body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    protected ResponseEntity<?> success(Object body, MultiValueMap<String, String> headers, HttpStatus httpStatus) {
        return new ResponseEntity<>(body, headers, httpStatus);
    }

}