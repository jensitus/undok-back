package at.undok.undok.client.api;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/service/undok/download")
@PreAuthorize("hasRole('USER')")
public interface DownloadApi {

    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> downloadClientCsv(@PathVariable String filename) throws IOException;

}
