package at.undok.common.api;

import at.undok.common.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/service/undok/ping")
public interface PingApi {

    @GetMapping
    ResponseEntity<String> ping();

    @GetMapping("pong")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<Message> pong();

}
