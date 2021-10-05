package at.undok.undok.client.api;

import at.undok.undok.client.service.DownloadService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RequestMapping("/service/undok/download")
public interface DownloadApi {

    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> downloadClientCsv(@PathVariable String filename) throws IOException;

}
