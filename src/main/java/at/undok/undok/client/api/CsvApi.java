package at.undok.undok.client.api;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/service/undok/csv")
@PreAuthorize("hasRole('USER')")
public interface CsvApi {

    @GetMapping("/fileNames")
    @PreAuthorize("hasRole('ADMIN')")
    List<String> getCsvFileNames();

    @GetMapping("/backupCsv/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Resource> downloadBackupCsv(@PathVariable("filename") String filename);

}
