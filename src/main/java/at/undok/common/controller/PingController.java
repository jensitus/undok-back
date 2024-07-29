package at.undok.common.controller;

import at.undok.common.api.PingApi;
import at.undok.common.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController implements PingApi {

    @Override
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }

    @Override
    public ResponseEntity<Message> pong() {
        return ResponseEntity.ok(new Message("ping pong"));
    }

}
