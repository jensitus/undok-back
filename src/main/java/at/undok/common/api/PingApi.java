package at.undok.common.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/service/undok/ping")
public interface PingApi {

    @GetMapping
    ResponseEntity<String> ping();

}
