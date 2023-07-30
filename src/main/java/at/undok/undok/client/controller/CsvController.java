package at.undok.undok.client.controller;

import at.undok.common.message.Message;
import at.undok.undok.client.api.CsvApi;
import at.undok.undok.client.exception.CsvNotFoundException;
import at.undok.undok.client.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CsvController implements CsvApi {

    private final CsvService csvService;

    @Override
    public List<String> getCsvFileNames() {
        return csvService.getCsvFileNames();
    }

    @ExceptionHandler(CsvNotFoundException.class)
    public ResponseEntity<Message> handleNullPointer(CsvNotFoundException csvNotFoundException) {
        return ResponseEntity.status(csvNotFoundException.getHttpStatus())
                             .body(new Message(csvNotFoundException.getErrorMessage()));
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
