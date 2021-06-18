package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.form.ClientForm;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RestController
@RequestMapping("/undok/clients")
public class ClientController {

    @PostMapping("/create")
    public ClientDto createClient(@RequestBody ClientForm clientForm) {
        return null;
    }

}
