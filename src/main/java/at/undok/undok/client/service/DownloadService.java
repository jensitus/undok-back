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
            Path file = Paths.get(filesPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("can't read file");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
