package at.undok.undok.client.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DownloadService {

    @Value("${files.path}")
    private String filesPath;

    public Resource download(String filename) {
        try {
            Path baseDir = Paths.get(filesPath).toAbsolutePath().normalize();
            Path file = baseDir.resolve(filename).normalize();
            // Reject any filename that escapes the base directory (e.g. "../../etc/passwd").
            if (!file.startsWith(baseDir)) {
                throw new RuntimeException("invalid filename");
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("can't read file");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("can't read file", e);
        }
    }

}
