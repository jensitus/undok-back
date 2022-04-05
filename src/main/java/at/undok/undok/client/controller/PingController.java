package at.undok.undok.client.controller;

import at.undok.undok.client.api.PingApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController implements PingApi {

    @Override
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }

}
