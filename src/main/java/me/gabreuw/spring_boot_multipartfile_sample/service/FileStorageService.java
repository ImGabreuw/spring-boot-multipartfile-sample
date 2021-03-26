package me.gabreuw.spring_boot_multipartfile_sample.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Log4j2
public class FileStorageService {

    private final Path fileStoragePath;
    private final String fileStorageLocation;

    public FileStorageService(
            @Value("${file.storage.location}") String fileStorageLocation
    ) {
        this.fileStorageLocation = fileStorageLocation;
        this.fileStoragePath = Paths.get(fileStorageLocation)
                .toAbsolutePath()
                .normalize();

        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(fileStoragePath);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException("Ocorreu um erro ao criar o diretório do arquivo.", e.getCause());
        }
    }

    public String storeFile(MultipartFile file) {
        String cleanedFileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = Paths.get(fileStoragePath + "\\" + cleanedFileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException("Ocorreu um erro ao copiar o arquivo para o diretório.", e.getCause());
        }

        return cleanedFileName;
    }

    public Resource downloadFile(String fileName) {
        Path path = Paths.get(fileStorageLocation)
                .toAbsolutePath()
                .resolve(fileName);
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            log.error(e);
            throw new RuntimeException("URL do arquivo está inválida ou mal formada.", e.getCause());
        }

        if (!(resource.exists() || resource.isReadable())) {
            throw new RuntimeException("O arquivo não existe ou está legível.");
        }

        return resource;
    }

    public MediaType getMediaTypeFromResource(Resource resource, HttpServletRequest request) {
        String mediaType;

        try {
            mediaType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            log.error("Ocorreu um erro ao identificar a MidiaType do arquivo.", e.getCause());
            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return MediaType.parseMediaType(mediaType);
    }
}
