package at.undok.undok.client.controller;

import at.undok.undok.client.api.CsvApi;
import at.undok.undok.client.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class CsvController implements CsvApi {

    private final CsvService csvService;

    @Override
    public Set<String> getCsvFileNames() {
        return csvService.getCsvFileNames();
    }

    @Override
    public ResponseEntity<Resource> downloadBackupCsv(String filename) {
        ByteArrayResource backupCsv = csvService.getBackupCsv(filename);
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                             .contentType(MediaType.parseMediaType("application/csv"))
                             .body(backupCsv);
    }
}
