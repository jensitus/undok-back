package at.undok.undok.client.api;

import at.undok.undok.client.model.entity.Fully;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/search")
@PreAuthorize("hasRole('USER')")
public interface SearchApi {

    @PostMapping
    ResponseEntity<Fully> searchFull(@RequestBody String searchTerm);

}
