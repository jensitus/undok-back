package at.undok.undok.client.controller;

import at.undok.undok.client.api.DownloadApi;
import at.undok.undok.client.service.DownloadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class DownloadController implements DownloadApi {

    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @Override
    public ResponseEntity<Resource> downloadClientCsv(String filename) throws IOException {
        Resource file = downloadService.download(filename);
        Path path = file.getFile().toPath();
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                             .body(file);
    }
}
