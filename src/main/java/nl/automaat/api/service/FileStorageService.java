package nl.automaat.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${automaat.upload-location}") String uploadLocation) {
        this.uploadRoot = Paths.get(uploadLocation, "registration").toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Kan upload-map niet aanmaken: " + uploadRoot, e);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Geen bestand ontvangen.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Alleen PDF-bestanden zijn toegestaan voor autopapieren.");
        }

        String fileName = UUID.randomUUID() + ".pdf";
        try {
            Path target = uploadRoot.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/registration/" + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Opslaan van bestand is mislukt.", e);
        }
    }

    public Resource loadAsResource(String storedPath) {
        try {
            String fileName = Paths.get(storedPath).getFileName().toString();
            Path file = uploadRoot.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("Bestand niet gevonden of niet leesbaar: " + fileName);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Ongeldig bestandspad: " + storedPath, e);
        }
    }
}
