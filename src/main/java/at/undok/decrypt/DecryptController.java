package at.undok.decrypt;

import org.springframework.http.ResponseEntity;

//@RestController
//@RequestMapping("decrypt")
public class DecryptController {

    private final DecryptService decryptService;

    public DecryptController(DecryptService decryptService) {
        this.decryptService = decryptService;
    }

//    @GetMapping
    public ResponseEntity<Void> getDecryption() {
        decryptService.getAddresses();
        return ResponseEntity.ok().build();
    }

}
