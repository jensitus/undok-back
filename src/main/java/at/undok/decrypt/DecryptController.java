package at.undok.decrypt;

import org.springframework.http.ResponseEntity;

//@RestController
//@RequestMapping("decrypt")
public class DecryptController {

    private final KeyService keyService;

    public DecryptController(KeyService keyService) {
        this.keyService = keyService;
    }

//    @GetMapping
    public ResponseEntity<Void> getDecryption() {
        // decryptService.getAddresses();
        return ResponseEntity.ok().build();
    }

}
