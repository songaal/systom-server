package io.gncloud.coin.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CommonController {
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
